package com.example.feedback.dto;

import com.example.feedback.entity.FeedbackComponent;

import java.time.LocalDateTime;

public record FeedbackComponentResponse(
        Long id,
        String name,
        String description,
        boolean active,
        Long applicationClientId,
        String applicationClientName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FeedbackComponentResponse from(FeedbackComponent component) {
        return new FeedbackComponentResponse(
                component.getId(),
                component.getName(),
                component.getDescription(),
                component.isActive(),
                component.getApplicationClient().getId(),
                component.getApplicationClient().getName(),
                component.getCreatedAt(),
                component.getUpdatedAt()
        );
    }
}
