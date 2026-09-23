package com.moodfit.dto;

import java.util.List;

public record RecommendationResponse(
        String mood,
        List<String> foods,
        List<String> music
) {
}
