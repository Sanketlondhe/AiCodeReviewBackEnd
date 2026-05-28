package com.example.aicodereview.service;


import com.example.aicodereview.dto.ReviewRequest;
import com.example.aicodereview.dto.ReviewResponse;
import com.example.aicodereview.exception.CodeReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeReviewServiceImpl implements CodeReviewService {

    private final ChatClient chatClient;

    // Spring loads the .st file from classpath:/prompts/
    @Value("classpath:templates/code-review-prompt.st")
    private Resource promptTemplate;

    @Override
    public ReviewResponse review(ReviewRequest request) {
        log.info("Starting code review | language={} | codeLength={}",
                request.getLanguage(), request.getCode().length());

        long startTime = System.currentTimeMillis();

        try {
            String prompt = buildPrompt(request);

            log.debug("Sending prompt to OpenAI | promptLength={}", prompt.length());

            String reviewText = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (!StringUtils.hasText(reviewText)) {
                log.error("OpenAI returned empty response for language={}", request.getLanguage());
                throw new CodeReviewException(
                        "AI returned an empty response",
                        "The AI service returned an empty response. Please try again.",
                        HttpStatus.BAD_GATEWAY
                );
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Code review completed | language={} | durationMs={} | responseLength={}",
                    request.getLanguage(), elapsed, reviewText.length());

            return ReviewResponse.success(reviewText, request.getLanguage(), elapsed);

        } catch (CodeReviewException ex) {
            throw ex; // Let GlobalExceptionHandler handle it

        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("Code review failed | language={} | durationMs={} | error={}",
                    request.getLanguage(), elapsed, ex.getMessage(), ex);

            throw new CodeReviewException(
                    "Failed to process code review: " + ex.getMessage(),
                    "Unable to complete the code review. Please try again.",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex
            );
        }
    }

    private String buildPrompt(ReviewRequest request) {
        try {
            PromptTemplate template = new PromptTemplate(promptTemplate);

            String requirements = StringUtils.hasText(request.getBusinessRequirements())
                    ? request.getBusinessRequirements()
                    : "No specific business requirements provided. Review for general best practices.";

            return template.render(Map.of(
                    "language", request.getLanguage(),
                    "businessRequirements", requirements,
                    "code", request.getCode()
            ));

        } catch (Exception ex) {
            log.error("Failed to build prompt template: {}", ex.getMessage(), ex);
            throw new CodeReviewException(
                    "Prompt template error: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex
            );
        }
    }
}
