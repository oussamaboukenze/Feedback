package com.example.feedback.dto;

import com.example.feedback.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        @Size(max = 120, message = "Le nom complet ne doit pas depasser 120 caracteres")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'adresse email n'est pas valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caracteres")
        String password,

        Role role
) {
}
