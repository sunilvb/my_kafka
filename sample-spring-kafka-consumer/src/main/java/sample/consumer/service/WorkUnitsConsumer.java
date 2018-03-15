package sample.consumer.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import sample.consumer.domain.WorkUnit;

@Service
public class WorkUnitsConsumer {
	private static final Logger log = LoggerFactory.getLogger(WorkUnitsConsumer.class);

	
	@KafkaListener(topics = "workunits")
	public void onReceiving(WorkUnit workUnit, @Header(KafkaHeaders.OFFSET) Integer offset,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Processing topic = {}, partition = {}, offset = {}, workUnit = {}", topic, partition, offset,
				workUnit);
		String url ="";
		String type = workUnit.getDefinition();
		if(type.endsWith("grade"))
			url = "http://localhost:8073/billing/product/lookup?id=" + workUnit.getId() + "&def=" + type;
		else
			url = "http://localhost:8073/billing/events/lookup?id=" + workUnit.getId() + "&def=" + type;
		
		log.info("Trying => " + url);
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		try {
			HttpResponse response = client.execute(post);
			int responseCode = response.getStatusLine().getStatusCode();

			if (responseCode != 200)
				log.error("Error Calling WebService :" + responseCode);
			/*
			msgCount++;
			if (msgCount == limit) {
				long endTime = System.nanoTime();
				HttpPost post2 = new HttpPost("http://localhost:8080/done?pid=S2&endTime=" + endTime);
				response = client.execute(post2);
				responseCode = response.getStatusLine().getStatusCode();

				if (responseCode != 200)
					log.error("Error Calling WebService http://localhost:8080/done?pid=S2&endTime=" + endTime
							+ " code : " + responseCode);
			}
			*/

		} catch (IOException e) {
			log.error(e.getMessage());
		}
		log.info("Called URL : " + url);

	}
}
