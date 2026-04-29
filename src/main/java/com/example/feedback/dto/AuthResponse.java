package com.example.feedback.dto;

import com.example.feedback.enums.Role;

public record AuthResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        String token
) {
}
