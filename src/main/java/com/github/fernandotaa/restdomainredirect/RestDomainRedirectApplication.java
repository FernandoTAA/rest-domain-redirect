package com.github.fernandotaa.restdomainredirect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RestDomainRedirectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestDomainRedirectApplication.class, args);
	}

}
