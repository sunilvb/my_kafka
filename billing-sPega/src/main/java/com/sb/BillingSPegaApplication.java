package com.sb;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BillingSPegaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingSPegaApplication.class, args);
	}
	
	@RequestMapping(path="/save")
	public Callable<ResponseEntity<?>> doLookup(@RequestParam String subscriberid , @RequestParam String billingrate, @RequestParam String adjust, @RequestParam String prorate)
	{
		int premium = Integer.parseInt(billingrate) +  Integer.parseInt(adjust);
		float bill = Float.parseFloat(billingrate) +  Float.parseFloat(adjust) + Float.parseFloat(adjust) ; 
		String url ="http://localhost:8088/save?subscriberid=" + subscriberid + "&rate=" + billingrate + "&adjustment=" + adjust + "&premium=" + premium + "&bill=" + bill;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		HttpResponse response;
		try {
			response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			System.out.println("Response = " + responseCode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(" Called ... " + url);
				
		return () -> ResponseEntity.ok("Done calling... ");
		
	}
}
