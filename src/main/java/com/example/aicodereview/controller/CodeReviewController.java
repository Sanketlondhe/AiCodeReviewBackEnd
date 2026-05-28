package com.example.aicodereview.controller;

import com.example.aicodereview.dto.ReviewRequest;
import com.example.aicodereview.dto.ReviewResponse;
import com.example.aicodereview.service.CodeReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    /**
     * POST /api/v1/review
     * Accepts code + metadata, returns a full AI-generated code review.
     */
    @PostMapping(
            value = "/review",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReviewResponse> review(
            @Valid @RequestBody ReviewRequest request,
            HttpServletRequest httpRequest) {

        log.info("POST /api/v1/review | ip={} | language={} | codeLength={}",
                getClientIp(httpRequest),
                request.getLanguage(),
                request.getCode().length());

        ReviewResponse response = codeReviewService.review(request);

        log.info("Review completed | language={} | processingTimeMs={}",
                response.getLanguage(), response.getProcessingTimeMs());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/health
     * Simple liveness check (Railway also uses /actuator/health).
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Code Review API is running");
    }

    // ── Extract real IP even behind a proxy ──
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
