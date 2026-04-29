package com.example.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationClientRequest(
        @NotBlank(message = "Le nom de l'application est obligatoire")
        @Size(max = 120, message = "Le nom ne doit pas depasser 120 caracteres")
        String name,

        @Size(max = 500, message = "La description ne doit pas depasser 500 caracteres")
        String description,

        Boolean active
) {
}
