package com.example.feedback.config;

import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.FeedbackComponent;
import com.example.feedback.repository.ApplicationClientRepository;
import com.example.feedback.repository.FeedbackComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner seedDemoData(
            ApplicationClientRepository applicationClientRepository,
            FeedbackComponentRepository feedbackComponentRepository
    ) {
        return args -> {
            if (applicationClientRepository.count() > 0) {
                return;
            }

            ApplicationClient applicationClient = applicationClientRepository.save(ApplicationClient.builder()
                    .name("Plateforme Demo")
                    .description("Application de demonstration pour collecter les avis utilisateurs.")
                    .active(true)
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
