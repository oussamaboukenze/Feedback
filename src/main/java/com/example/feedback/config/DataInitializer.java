package com.example.feedback.config;

import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.FeedbackComponent;
import com.example.feedback.entity.User;
import com.example.feedback.enums.Role;
import com.example.feedback.repository.ApplicationClientRepository;
import com.example.feedback.repository.FeedbackComponentRepository;
import com.example.feedback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner seedDemoData(
            ApplicationClientRepository applicationClientRepository,
            FeedbackComponentRepository feedbackComponentRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (applicationClientRepository.count() > 0) {
                return;
            }

            User admin = userRepository.findByEmailIgnoreCase("admin@demo.com")
                    .orElseGet(() -> userRepository.save(User.builder()
                            .fullName("Admin")
                            .email("admin@demo.com")
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .build()));

            ApplicationClient applicationClient = applicationClientRepository.save(ApplicationClient.builder()
                    .name("Plateforme Demo")
                    .description("Application de demonstration pour collecter les avis utilisateurs.")
                    .active(true)
                    .owner(admin)
                    .build());

            feedbackComponentRepository.saveAll(List.of(
                    FeedbackComponent.builder()
                            .name("Interface")
                            .description("Experience visuelle et navigation.")
                            .applicationClient(applicationClient)
                            .active(true)
                            .build(),
                    FeedbackComponent.builder()
                            .name("Performance")
                            .description("Rapidite, disponibilite et stabilite.")
                            .applicationClient(applicationClient)
                            .active(true)
                            .build(),
                    FeedbackComponent.builder()
                            .name("Fonctionnalites")
                            .description("Qualite des services proposes aux utilisateurs.")
                            .applicationClient(applicationClient)
                            .active(true)
                            .build()
            ));
        };
    }
}
