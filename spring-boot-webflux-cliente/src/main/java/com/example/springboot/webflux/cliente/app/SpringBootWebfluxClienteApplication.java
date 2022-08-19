package com.example.springboot.webflux.cliente.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringBootWebfluxClienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxClienteApplication.class, args);
	}

}
