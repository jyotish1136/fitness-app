package com.fitness.activityservice.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserValidateService {
    private final WebClient webClient;
    public Boolean validateUser(String userId) {
        try {
            return webClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
