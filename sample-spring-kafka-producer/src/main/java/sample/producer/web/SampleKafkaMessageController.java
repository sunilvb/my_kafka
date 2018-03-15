package sample.producer.web;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVReader;

import sample.producer.domain.WorkUnit;
import sample.producer.service.WorkUnitDispatcher;

@RestController
public class SampleKafkaMessageController {

	@Autowired
	private WorkUnitDispatcher workUnitDispatcher;

	private long startTime,et;
	private long duration;

	/*
	 * @GetMapping("/generateWork") public boolean sendMessage(WorkUnit
	 * workUnit) { return this.workUnitDispatcher.dispatch(workUnit); }
	 */
	@GetMapping("/generateWork")
	public String sendMessage(@RequestParam(value="count", defaultValue="1") int count, @RequestParam(value="loops", defaultValue="1") int loops) {

		startTime = System.nanoTime();
		String ret = "";
		MyProducerThread[] threads = new MyProducerThread[count];
		for (int i = 0; i < count; i++) {
			threads[i] = new MyProducerThread(loops, workUnitDispatcher);
			threads[i].start();
		}

		for (int j = 0; j < threads.length; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		et = System.nanoTime();
		duration = (et - startTime);
		System.out.println("===========================================================");
		System.out.println("Total run time for S1 = " + (duration / 1000000) + " Milli Seconds");
		System.out.println("===========================================================");
		
		return "File processing started...: true <br>File processing finished..." + ret;
	}

	@RequestMapping("*")
	@ResponseBody
	public String fallbackMethod() {
		return "Default Response for all requests to this resource";
	}

	@RequestMapping("/done")
	@ResponseBody
	public Callable<ResponseEntity<?>> done(@RequestParam long endTime, @RequestParam String pid) {

		duration = (endTime - startTime);
		System.out.println("===========================================================");
		System.out.println("Total run time for " + pid + "= " + (duration / 1000000) + " Milli Seconds");
		System.out.println("===========================================================");

		return () -> ResponseEntity.ok("Done calling... ");
	}
}

class MyProducerThread extends Thread {
	private int loops;
	WorkUnitDispatcher workDispatcher;

	public MyProducerThread(int lp, WorkUnitDispatcher workUnitDispatcher) {
		this.loops = lp;
		this.workDispatcher = workUnitDispatcher;
	}

	@Override
	public void run() {
		for (int i = 0; i < loops; i++) {
			CSVReader reader = null;
			try {
				reader = new CSVReader(new FileReader("billing_input.csv"));
				String[] nextLine;
				while ((nextLine = reader.readNext()) != null) {
					WorkUnit workUnit = new WorkUnit(nextLine[0], nextLine[1]);
					this.workDispatcher.dispatch(workUnit);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
