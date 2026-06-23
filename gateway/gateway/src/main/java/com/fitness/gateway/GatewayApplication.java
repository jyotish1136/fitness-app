package com.fitness.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(GatewayApplication.class, args);
		Environment env = run.getEnvironment();

		System.out.println("Mongo URI = " +
				env.getProperty("api.name"));
		System.out.println("Mongo URI = " +
				env.getProperty("eureka.client.serviceUrl.defaultZone"));
		System.out.println("Mongo URI = " +
				env.getProperty("server.port"));
	}

}
