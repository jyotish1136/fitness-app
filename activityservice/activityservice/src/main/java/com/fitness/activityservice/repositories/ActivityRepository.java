package com.fitness.activityservice.repositories;

import com.fitness.activityservice.entities.Activity;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    Activity getById(String id);
}
