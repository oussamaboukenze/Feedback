package com.example.feedback.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.feedback.dto.ApplicationClientRequest;
import com.example.feedback.dto.ApplicationClientResponse;
import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.User;
import com.example.feedback.repository.ApplicationClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationClientService {

    private final ApplicationClientRepository applicationClientRepository;
    private final AuthService authService;

    @Transactional
    public ApplicationClientResponse create(ApplicationClientRequest request) {
        User currentUser = authService.getCurrentUser();
        String name = request.name().trim();
        if (applicationClientRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une application avec ce nom existe deja");
        }
        ApplicationClient applicationClient = ApplicationClient.builder()
                .name(name)
                .description(clean(request.description()))
                .active(request.active() == null || request.active())
                .owner(currentUser)
                .build();
        return ApplicationClientResponse.from(applicationClientRepository.save(applicationClient));
    }

    @Transactional(readOnly = true)
    public List<ApplicationClientResponse> findAll() {
        User currentUser = authService.getCurrentUser();
        return applicationClientRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId())
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
        checkOwnership(applicationClient);
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
        checkOwnership(applicationClient);
        applicationClientRepository.delete(applicationClient);
    }

    @Transactional(readOnly = true)
    public ApplicationClient getEntity(Long id) {
        return applicationClientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application introuvable"));
    }

    private void checkOwnership(ApplicationClient app) {
        User currentUser = authService.getCurrentUser();
        if (!app.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces interdit");
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Transactional(readOnly = true)
    public List<ApplicationClientResponse> findAllPublic() {
        return applicationClientRepository.findAllByActiveOrderByNameAsc(true)
            .stream()
            .map(ApplicationClientResponse::from)
            .toList();
}
}