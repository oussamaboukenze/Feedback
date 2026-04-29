package com.example.feedback.service;

import com.example.feedback.dto.FeedbackComponentRequest;
import com.example.feedback.dto.FeedbackComponentResponse;
import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.FeedbackComponent;
import com.example.feedback.repository.FeedbackRepository;
import com.example.feedback.repository.FeedbackComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackComponentService {

    private final FeedbackComponentRepository feedbackComponentRepository;
    private final FeedbackRepository feedbackRepository;
    private final ApplicationClientService applicationClientService;

    @Transactional
    public FeedbackComponentResponse create(Long applicationClientId, FeedbackComponentRequest request) {
        ApplicationClient applicationClient = applicationClientService.getEntity(applicationClientId);
        String name = request.name().trim();

        if (feedbackComponentRepository.existsByApplicationClientIdAndNameIgnoreCase(applicationClientId, name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce composant existe deja pour cette application");
        }

        FeedbackComponent component = FeedbackComponent.builder()
                .name(name)
                .description(clean(request.description()))
                .active(request.active() == null || request.active())
                .applicationClient(applicationClient)
                .build();

        return FeedbackComponentResponse.from(feedbackComponentRepository.save(component));
    }

    @Transactional(readOnly = true)
    public List<FeedbackComponentResponse> findByApplication(Long applicationClientId) {
        applicationClientService.getEntity(applicationClientId);
        return feedbackComponentRepository.findByApplicationClientIdOrderByNameAsc(applicationClientId)
                .stream()
                .map(FeedbackComponentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackComponentResponse findById(Long id) {
        return FeedbackComponentResponse.from(getEntity(id));
    }

    @Transactional
    public FeedbackComponentResponse update(Long id, FeedbackComponentRequest request) {
        FeedbackComponent component = getEntity(id);
        String name = request.name().trim();
        Long applicationClientId = component.getApplicationClient().getId();

        feedbackComponentRepository.findByApplicationClientIdOrderByNameAsc(applicationClientId)
                .stream()
                .filter(existing -> existing.getName().equalsIgnoreCase(name) && !existing.getId().equals(id))
                .findFirst()
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce composant existe deja pour cette application");
                });

        component.setName(name);
        component.setDescription(clean(request.description()));
        if (request.active() != null) {
            component.setActive(request.active());
        }

        return FeedbackComponentResponse.from(feedbackComponentRepository.save(component));
    }

    @Transactional
    public void delete(Long id) {
        FeedbackComponent component = getEntity(id);
        feedbackRepository.findByComponentId(id).forEach(feedback -> feedback.setComponent(null));
        feedbackComponentRepository.delete(component);
    }

    @Transactional(readOnly = true)
    public FeedbackComponent getEntity(Long id) {
        return feedbackComponentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Composant introuvable"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
