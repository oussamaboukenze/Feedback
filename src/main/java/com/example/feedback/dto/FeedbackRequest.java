package com.example.feedback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @NotNull(message = "L'application est obligatoire")
        Long applicationClientId,

        Long componentId,

        @NotNull(message = "La note est obligatoire")
        @Min(value = 1, message = "La note minimale est 1")
        @Max(value = 5, message = "La note maximale est 5")
        Integer rating,

        @NotBlank(message = "Le commentaire est obligatoire")
        @Size(max = 2000, message = "Le commentaire ne doit pas depasser 2000 caracteres")
        String comment,

        @Size(max = 120, message = "Le nom ne doit pas depasser 120 caracteres")
        String authorName,

        @Email(message = "L'adresse email n'est pas valide")
        @Size(max = 160, message = "L'email ne doit pas depasser 160 caracteres")
        String authorEmail
) {
}
