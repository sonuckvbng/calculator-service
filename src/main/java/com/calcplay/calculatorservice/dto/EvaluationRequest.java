package com.calcplay.calculatorservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EvaluationRequest(
        @NotBlank(message = "Expression is required")
        @Size(max = 300, message = "Expression must not exceed 300 characters")
        String expression) {
}
