package com.example.aicodereview.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "Programming language is required")
    @Size(max = 50, message = "Language name must not exceed 50 characters")
    private String language;

    @Size(max = 2000, message = "Business requirements must not exceed 2000 characters")
    private String businessRequirements;

    @NotBlank(message = "Code is required")
    @Size(min = 10, message = "Code must be at least 10 characters")
    @Size(max = 10000, message = "Code must not exceed 10000 characters")
    private String code;

}
