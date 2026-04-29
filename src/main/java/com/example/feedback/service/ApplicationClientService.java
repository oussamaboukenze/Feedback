package com.example.feedback.service;

import com.example.feedback.dto.ApplicationClientRequest;
import com.example.feedback.dto.ApplicationClientResponse;
import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.repository.ApplicationClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationClientService {

    private final ApplicationClientRepository applicationClientRepository;

    @Transactional
    public ApplicationClientResponse create(ApplicationClientRequest request) {
        String name = request.name().trim();
        if (applicationClientRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une application avec ce nom existe deja");
        }

        ApplicationClient applicationClient = ApplicationClient.builder()
                .name(name)
                .description(clean(request.description()))
                .active(request.active() == null || request.active())
                .build();

        return ApplicationClientResponse.from(applicationClientRepository.save(applicationClient));
    }

    @Transactional(readOnly = true)
    public List<ApplicationClientResponse> findAll() {
        return applicationClientRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ApplicationClientResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationClientResponse findById(Long id) {
        return ApplicationClientResponse.from(getEntity(id));
    }

    @Transactional
    public ApplicationClientResponse update(Long id, ApplicationClientRequest request) {
        ApplicationClient applicationClient = getEntity(id);
        String name = request.name().trim();

        applicationClientRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une application avec ce nom existe deja");
                });

        applicationClient.setName(name);
        applicationClient.setDescription(clean(request.description()));
        if (request.active() != null) {
            applicationClient.setActive(request.active());
        }

        return ApplicationClientResponse.from(applicationClientRepository.save(applicationClient));
    }

    @Transactional
    public void delete(Long id) {
        ApplicationClient applicationClient = getEntity(id);
        applicationClientRepository.delete(applicationClient);
    }

    @Transactional(readOnly = true)
    public ApplicationClient getEntity(Long id) {
        return applicationClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application introuvable"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
