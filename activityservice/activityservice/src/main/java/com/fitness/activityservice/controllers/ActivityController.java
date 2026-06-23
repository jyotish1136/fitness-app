package com.fitness.activityservice.controllers;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.services.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@AllArgsConstructor
public class ActivityController {
    private ActivityService activityService;
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request){
        ActivityResponse activityResponse = activityService.addActivity(request);
        if (activityResponse != null){
            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<ActivityResponse> getAllActivities(@PathVariable String id){
        return ResponseEntity.ok(activityService.get(id));
    }
}
