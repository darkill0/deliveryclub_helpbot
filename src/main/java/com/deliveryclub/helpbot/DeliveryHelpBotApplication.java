package com.deliveryclub.helpbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DeliveryHelpBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryHelpBotApplication.class, args);
	}

}
