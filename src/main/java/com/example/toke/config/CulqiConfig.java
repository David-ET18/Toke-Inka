package com.example.toke.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CulqiConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Creamos una instancia de RestTemplate para hacer llamadas a Culqi
        return new RestTemplate();
    }
}