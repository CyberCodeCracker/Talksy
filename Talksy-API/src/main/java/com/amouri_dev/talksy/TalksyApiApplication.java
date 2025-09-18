package com.amouri_dev.talksy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TalksyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalksyApiApplication.class, args);
	}

}
