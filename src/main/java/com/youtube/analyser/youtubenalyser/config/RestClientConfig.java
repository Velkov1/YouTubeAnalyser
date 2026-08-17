package com.youtube.analyser.youtubenalyser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${youTube.base-url}")
    private String url;

    @Bean
    public RestClient youTubeRestClient(){
        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
}
