package com.fitness.userservice.services;

import com.fitness.userservice.dto.UserRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.entities.User;
import com.fitness.userservice.repositories.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    public UserResponse save(UserRequest userRequest) {
        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        User userSaved = userRepo.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userSaved.getId());
        userResponse.setEmail(userSaved.getEmail());
        userResponse.setFirstName(userSaved.getFirstName());
        userResponse.setLastName(userSaved.getLastName());
        userResponse.setPassword(userSaved.getPassword());
        userResponse.setCreatedDate(userSaved.getCreatedAt());
        userResponse.setUpdatedDate(userSaved.getUpdatedAt());
        return userResponse;
    }

    public UserResponse getUser(String userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setPassword(user.getPassword());
        userResponse.setCreatedDate(user.getCreatedAt());
        userResponse.setUpdatedDate(user.getUpdatedAt());
        return userResponse;
    }

    public Boolean validateUser(String userId) {
        return userRepo.existsById(userId);
    }
}
