package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class AppConfig {

    @Value("${employee.api.base-url:http://localhost:8112/api/v1/employee}")
    private String employeeApiBaseUrl;

    @Bean(name = "employeeRestTemplate")
    public RestTemplate employeeRestTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(employeeApiBaseUrl));
        return restTemplate;
    }
}
