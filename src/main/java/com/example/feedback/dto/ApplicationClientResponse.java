package com.example.feedback.dto;

import com.example.feedback.entity.ApplicationClient;

import java.time.LocalDateTime;

public record ApplicationClientResponse(
        Long id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ApplicationClientResponse from(ApplicationClient applicationClient) {
        return new ApplicationClientResponse(
                applicationClient.getId(),
                applicationClient.getName(),
                applicationClient.getDescription(),
                applicationClient.isActive(),
                applicationClient.getCreatedAt(),
                applicationClient.getUpdatedAt()
        );
    }
}
