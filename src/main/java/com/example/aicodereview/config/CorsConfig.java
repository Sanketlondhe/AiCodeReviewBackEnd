package com.example.aicodereview.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class CorsConfig implements WebMvcConfigurer {

    @Value("${FRONTEND_URL:https://ai-code-review-lac.vercel.app}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS for: {}", frontendUrl);

        registry.addMapping("/api/**")
                .allowedOrigins(
                        frontendUrl,
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}