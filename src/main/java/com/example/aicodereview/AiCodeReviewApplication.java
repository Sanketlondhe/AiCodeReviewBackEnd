package com.example.aicodereview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class AiCodeReviewApplication {

    public static void main(String[] args) {

        SpringApplication.run(AiCodeReviewApplication.class, args);
        log.info("====================================================");
        log.info("  AI Code Review Assistant — Started Successfully");
        log.info("====================================================");
    }

}
