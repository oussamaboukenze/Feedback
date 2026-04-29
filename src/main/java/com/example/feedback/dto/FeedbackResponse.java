package com.example.feedback.dto;

import com.example.feedback.entity.Feedback;
import com.example.feedback.entity.FeedbackComponent;
import com.example.feedback.enums.FeedbackStatus;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        Integer rating,
        String comment,
        String authorName,
        String authorEmail,
        FeedbackStatus status,
        Long applicationClientId,
        String applicationClientName,
        Long componentId,
        String componentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FeedbackResponse from(Feedback feedback) {
        FeedbackComponent component = feedback.getComponent();
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getAuthorName(),
                feedback.getAuthorEmail(),
                feedback.getStatus(),
                feedback.getApplicationClient().getId(),
                feedback.getApplicationClient().getName(),
                component == null ? null : component.getId(),
                component == null ? null : component.getName(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );
    }
}
