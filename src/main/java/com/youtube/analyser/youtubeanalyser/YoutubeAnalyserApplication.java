package com.youtube.analyser.youtubeanalyser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YoutubeAnalyserApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeAnalyserApplication.class, args);
	}

}
