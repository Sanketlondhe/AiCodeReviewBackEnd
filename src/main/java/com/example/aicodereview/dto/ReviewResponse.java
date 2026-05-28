package com.example.aicodereview.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private boolean success;
    private String review;          // Full raw review text from AI
    private String language;
    private long processingTimeMs;
    private LocalDateTime reviewedAt;
    private String errorMessage;    // Populated only on failure

    // Static factory — success case
    public static ReviewResponse success(String review, String language, long processingTimeMs) {
        return ReviewResponse.builder()
                .success(true)
                .review(review)
                .language(language)
                .processingTimeMs(processingTimeMs)
                .reviewedAt(LocalDateTime.now())
                .build();
    }

    // Static factory — error case
    public static ReviewResponse error(String errorMessage) {
        return ReviewResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .reviewedAt(LocalDateTime.now())
                .build();
    }
}
