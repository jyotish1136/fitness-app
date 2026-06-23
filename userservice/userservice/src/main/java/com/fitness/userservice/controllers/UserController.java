package com.fitness.userservice.controllers;

import com.fitness.userservice.dto.UserRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        UserResponse userResponse = userService.getUser(userId);
        if (userResponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userResponse);
    }
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.save(userRequest);
        if(userResponse!=null){
            return ResponseEntity.ok(userResponse);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.validateUser(userId));
    }
}
