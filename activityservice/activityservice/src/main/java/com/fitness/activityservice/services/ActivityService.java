package com.fitness.activityservice.services;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.entities.Activity;
import com.fitness.activityservice.repositories.ActivityRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidateService userValidateService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse addActivity(ActivityRequest request) {
        Boolean b = userValidateService.validateUser(request.getUserId());
        if (!b) {
            throw new RuntimeException("Invalid user "+ request.getUserId());
        }
        Activity response = Activity.builder()
                .activityType(request.getActivityType())
                .userId(request.getUserId())
                .additionalMetrics(request.getAdditionalMetrics())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .caloriesBurn(request.getCaloriesBurn())
                .build();
        Activity save = activityRepository.save(response);

        try {
            kafkaTemplate.send(topicName,save.getUserId(),save);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapActivityResponse(save);
    }

    private ActivityResponse mapActivityResponse(Activity save) {
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(save.getId());
        activityResponse.setActivityType(save.getActivityType());
        activityResponse.setAdditionalMetrics(save.getAdditionalMetrics());
        activityResponse.setDuration(save.getDuration());
        activityResponse.setStartTime(save.getStartTime());
        activityResponse.setCaloriesBurn(save.getCaloriesBurn());
        activityResponse.setUserId(save.getUserId());
        activityResponse.setCreatedAt(save.getCreatedAt());
        activityResponse.setUpdatedAt(save.getUpdatedAt());
        return activityResponse;
    }

    public ActivityResponse get(String id) {
        Activity activity = activityRepository.getById(id);
        return mapActivityResponse(activity);
    }
}
