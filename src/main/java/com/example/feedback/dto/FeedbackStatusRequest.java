package com.example.feedback.dto;

import com.example.feedback.enums.FeedbackStatus;
import jakarta.validation.constraints.NotNull;

public record FeedbackStatusRequest(
        @NotNull(message = "Le statut est obligatoire")
        FeedbackStatus status
) {
}
