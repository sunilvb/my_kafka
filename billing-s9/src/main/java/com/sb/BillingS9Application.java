package com.sb;

import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Random;


@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class BillingS9Application {

	private static final Logger log = LoggerFactory.getLogger(BillingS9Application.class);
	private String[] upgrade = {"&type=product&rate=200&adjustment=50&premium=250&bill=275","&type=product&rate=210&adjustment=50&premium=260&bill=278","&type=product&rate=220&adjustment=70&premium=270&bill=285"};
	private String[] downgrade = {"&type=product&rate=200&adjustment=-50&premium=150&bill=175","&type=product&rate=170&adjustment=-40&premium=1300&bill=115","&type=product&rate=150&adjustment=-30&premium=120&bill=100"};	
	public static void main(String[] args) {
		SpringApplication.run(BillingS9Application.class, args);
	}
	
	@RequestMapping("/lookup")
	public Callable<ResponseEntity<?>> doLookup(@RequestParam String id, @RequestParam String def) {
		HttpClient client = HttpClientBuilder.create().build();
		String url2 = "";
		int i = new Random().nextInt(3);
		if("Upgrade".equalsIgnoreCase(def))
			url2="http://localhost:8088/save2?subscriberid=" + id + upgrade[i];
		else
			url2="http://localhost:8088/save2?subscriberid=" + id + downgrade[i];
		log.info("Calling = > " + url2);
		HttpPost post2 = new HttpPost(url2);
		HttpResponse response = null;
		try {
			response = client.execute(post2);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		int responseCode = response.getStatusLine().getStatusCode();

		if (responseCode != 200)
			log.error("Error Calling WebService :" + responseCode);
		
		return () -> ResponseEntity.ok("Done calling... ");
	}
}
