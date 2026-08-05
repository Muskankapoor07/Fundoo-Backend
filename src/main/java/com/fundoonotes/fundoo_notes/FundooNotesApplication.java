package com.fundoonotes.fundoo_notes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FundooNotesApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		System.out.println("JVM timezone set to Asia/Kolkata: " + java.time.LocalDateTime.now());
	}

	public static void main(String[] args) {
		SpringApplication.run(FundooNotesApplication.class, args);
	}

}
