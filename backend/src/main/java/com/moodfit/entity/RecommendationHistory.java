package com.moodfit.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecommendationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer heartRate;
    private Double temperature;
    private String weather;
    private String mood;

    @ElementCollection
    private List<String> foods = new ArrayList<>();

    @ElementCollection
    private List<String> music = new ArrayList<>();

    private LocalDateTime createdAt;

    protected RecommendationHistory() {
    }

    public RecommendationHistory(Integer heartRate, Double temperature, String weather, String mood, List<String> foods, List<String> music) {
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.weather = weather;
        this.mood = mood;
        this.foods = foods;
        this.music = music;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getWeather() {
        return weather;
    }

    public String getMood() {
        return mood;
    }

    public List<String> getFoods() {
        return foods;
    }

    public List<String> getMusic() {
        return music;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
