package com.sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class BillingZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingZuulApplication.class, args);
	}
}
