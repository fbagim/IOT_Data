package com.iot.api.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig to configure OpenApi API documentation
 */
@Configuration
public class SwaggerConfig {
    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("events").packagesToScan("com.test.iot.controller").pathsToMatch("/**").build();
    }
}
