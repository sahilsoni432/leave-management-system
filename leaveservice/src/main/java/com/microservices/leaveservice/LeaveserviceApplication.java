package com.microservices.leaveservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LeaveserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaveserviceApplication.class, args);
	}

}
