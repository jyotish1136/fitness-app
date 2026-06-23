package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.entities.Activity;
import com.fitness.aiservice.entities.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    private final ActivityAIService activityAIService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RecommendationService recommendationService;
    @KafkaListener(
            topics = "${kafka.topic.name}",
            groupId = "activity-processor-group"
    )
    public void processMessage(Activity activity) {

        log.info("Received activity for userId={}", activity.getUserId());

        try {
            String aiRawResponse = activityAIService.generateRecommendation(activity);

            if (aiRawResponse == null || aiRawResponse.isBlank()) {
                log.warn("AI response is empty for userId={}", activity.getUserId());
                getDefaultRecommendation(activity);
                return;
            }

            Recommendation recommendation = processAIResponse(activity, aiRawResponse);

            log.info("Recommendation generated successfully for userId={}", activity.getUserId());
            recommendationService.saveRecommendation(recommendation);
            return;
        } catch (Exception e) {
            log.error("Unexpected error while processing activity userId={}",
                    activity.getUserId(), e);
            recommendationService.saveRecommendation(getDefaultRecommendation(activity));
            return;
        }
    }

    private Recommendation processAIResponse(Activity activity, String response) {

        try {
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                log.warn("AI text node missing for userId={}", activity.getUserId());
                return getDefaultRecommendation(activity);
            }

            return convertToPojo(
                    textNode.asText(),
                    activity.getUserId(),
                    activity.getId()
            );

        } catch (Exception e) {
            log.error("Error parsing AI response for userId={}",
                    activity.getUserId(), e);

            return getDefaultRecommendation(activity);
        }
    }

    private Recommendation convertToPojo(String json,
                                         String userId,
                                         String activityId) throws Exception {

        // 🔥 Clean markdown if present
        json = cleanMarkdown(json);

        // 🔥 Extract pure JSON safely
        json = extractPureJson(json);

        JsonNode root = objectMapper.readTree(json);

        Recommendation recommendation = new Recommendation();
        recommendation.setUserId(userId);
        recommendation.setActivityId(activityId);

        JsonNode analysis = root.path("analysis");

        String combinedAnalysis =
                "Overall: " + analysis.path("overall").asText("") + "\n\n" +
                        "Pace: " + analysis.path("pace").asText("") + "\n\n" +
                        "Heart Rate: " + analysis.path("heartRate").asText("") + "\n\n" +
                        "Calories Burned: " + analysis.path("caloriesBurned").asText("");

        recommendation.setRecommendation(combinedAnalysis);

        // Improvements
        List<String> improvements = new ArrayList<>();
        for (JsonNode node : root.path("improvements")) {
            improvements.add(
                    node.path("area").asText("") + ": " +
                            node.path("recommendation").asText("")
            );
        }
        recommendation.setImprovements(improvements);

        // Suggestions
        List<String> suggestions = new ArrayList<>();
        for (JsonNode node : root.path("suggestions")) {
            suggestions.add(
                    node.path("workout").asText("") + ": " +
                            node.path("description").asText("")
            );
        }
        recommendation.setSuggestions(suggestions);

        // Safety
        List<String> safetyList = new ArrayList<>();
        for (JsonNode node : root.path("safety")) {
            safetyList.add(node.asText(""));
        }
        recommendation.setSafety(safetyList);

        return recommendation;
    }

    // ✅ Removes ```json and ``` safely
    private String cleanMarkdown(String json) {
        return json
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    // ✅ Extracts only valid JSON block
    private String extractPureJson(String response) {

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }

        return response;
    }

    private Recommendation getDefaultRecommendation(Activity activity) {

        log.info("Returning default recommendation for userId={}",
                activity.getUserId());

        return Recommendation.builder()
                .userId(activity.getUserId())
                .activityId(activity.getId())
                .recommendation(
                        "Great job completing your activity! Keep maintaining consistency and gradually improve your performance over time."
                )
                .improvements(List.of(
                        "Maintain consistent workout schedule",
                        "Focus on proper form and breathing",
                        "Increase intensity gradually over time"
                ))
                .suggestions(List.of(
                        "Stay hydrated before and after workouts",
                        "Include warm-up and cool-down sessions",
                        "Track your progress weekly"
                ))
                .safety(List.of(
                        "Listen to your body and avoid overtraining",
                        "Take rest days when needed",
                        "Consult a professional if experiencing pain"
                ))
                .build();
    }
}