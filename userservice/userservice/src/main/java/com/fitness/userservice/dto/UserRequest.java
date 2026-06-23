package com.fitness.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message ="Password is required")
    @Size(min=6, max=16, message = "Password must have length >= 6 && <=16")
    private String password;
    private String firstName;
    private String lastName;
}
