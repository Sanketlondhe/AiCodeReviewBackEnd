package com.example.aicodereview.service;


import com.example.aicodereview.dto.ReviewRequest;
import com.example.aicodereview.dto.ReviewResponse;

public interface CodeReviewService {

    /**
     * Sends the code to OpenAI via Spring AI and returns a structured review.
     *
     * @param request contains language, businessRequirements, code
     * @return ReviewResponse with the AI-generated review
     */
    ReviewResponse review(ReviewRequest request);
}