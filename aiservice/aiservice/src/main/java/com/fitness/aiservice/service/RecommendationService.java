package com.fitness.aiservice.service;

import com.fitness.aiservice.entities.Recommendation;
import com.fitness.aiservice.repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    public List<Recommendation> getUserRecommendation(String userId){
        return recommendationRepository.findByUserId(userId);
    }
    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId).orElseThrow(()->new RuntimeException("activityId not found"));
    }

    public void saveRecommendation(Recommendation recommendation) {
        if (recommendation == null) {
            throw new NullPointerException("recommendation cannot be null");
        }
        Recommendation save = recommendationRepository.save(recommendation);
        System.out.println("save recommendation");
    }
}
