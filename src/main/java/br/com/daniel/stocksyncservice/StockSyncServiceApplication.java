package br.com.daniel.stocksyncservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication
public class StockSyncServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockSyncServiceApplication.class, args);
	}

}
