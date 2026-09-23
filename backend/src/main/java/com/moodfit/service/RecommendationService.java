package com.moodfit.service;

import com.moodfit.dto.RecommendationRequest;
import com.moodfit.dto.RecommendationResponse;
import com.moodfit.entity.RecommendationHistory;
import com.moodfit.repository.RecommendationHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private final RecommendationHistoryRepository recommendationHistoryRepository;

    public RecommendationService(RecommendationHistoryRepository recommendationHistoryRepository) {
        this.recommendationHistoryRepository = recommendationHistoryRepository;
    }

    public RecommendationResponse recommend(RecommendationRequest request) {
        String mood = analyzeMood(request.heartRate(), request.temperature(), request.weather());
        List<String> foods = recommendFoods(mood);
        List<String> music = recommendMusic(mood);

        recommendationHistoryRepository.save(new RecommendationHistory(
                request.heartRate(),
                request.temperature(),
                request.weather(),
                mood,
                foods,
                music
        ));

        return new RecommendationResponse(mood, foods, music);
    }

    private String analyzeMood(Integer heartRate, Double temperature, String weather) {
        String normalizedWeather = weather.toLowerCase();

        if (heartRate >= 100 || temperature >= 30) {
            return "활동적";
        }

        if (heartRate <= 60 || normalizedWeather.contains("rain") || normalizedWeather.contains("비")) {
            return "차분함";
        }

        if (temperature <= 5 || normalizedWeather.contains("snow") || normalizedWeather.contains("눈")) {
            return "포근함 필요";
        }

        return "상쾌함";
    }

    private List<String> recommendFoods(String mood) {
        return switch (mood) {
            case "활동적" -> List.of("그릴드 치킨 샐러드", "바나나 스무디", "포케");
            case "차분함" -> List.of("따뜻한 수프", "리조또", "허브티");
            case "포근함 필요" -> List.of("우동", "김치찌개", "핫초코");
            default -> List.of("샌드위치", "요거트 볼", "아이스 아메리카노");
        };
    }

    private List<String> recommendMusic(String mood) {
        return switch (mood) {
            case "활동적" -> List.of("Upbeat Pop", "Dance Workout", "K-Pop Energy");
            case "차분함" -> List.of("Lo-fi Beats", "Acoustic Calm", "Piano Focus");
            case "포근함 필요" -> List.of("Warm Jazz", "Coffeehouse Acoustic", "Soft Ballad");
            default -> List.of("Morning Indie", "Fresh Pop", "City Walk");
        };
    }
}
