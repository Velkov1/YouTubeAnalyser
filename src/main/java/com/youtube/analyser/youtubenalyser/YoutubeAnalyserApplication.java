package com.youtube.analyser.youtubenalyser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YoutubenalyserApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubenalyserApplication.class, args);
	}

}
