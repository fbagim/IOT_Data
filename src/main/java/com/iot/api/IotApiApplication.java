package com.iot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
@EnableKafka
@SpringBootApplication
@EnableWebSecurity
public class IotApiApplication {
	/**
	 * SpringApplication -Entry point to start all services
	 * @param args
	 */
	public static void main(String[] args) 
	{
		SpringApplication.run(IotApiApplication.class, args);
	}
}
