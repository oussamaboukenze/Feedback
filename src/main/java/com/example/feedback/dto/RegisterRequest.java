package com.example.feedback.dto;

import com.example.feedback.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(max = 120) String fullName,
    @NotBlank @Email           String email,
    @NotBlank @Size(min = 6)   String password
    // plus de champ role — toujours USER à l'inscription
) {}
