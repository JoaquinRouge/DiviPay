package com.divipay.spent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpentApplication.class, args);
	}

}
