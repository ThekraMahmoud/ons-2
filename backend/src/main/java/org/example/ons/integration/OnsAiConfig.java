package org.example.ons.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OnsAiConfig {

    @Bean
    public RestClient onsAiRestClient(@Value("${ons.ai.base-url:http://localhost:8000}") String baseUrl) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}