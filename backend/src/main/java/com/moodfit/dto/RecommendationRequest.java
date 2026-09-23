package com.moodfit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationRequest(
        @NotNull Integer heartRate,
        @NotNull Double temperature,
        @NotBlank String weather
) {
}
