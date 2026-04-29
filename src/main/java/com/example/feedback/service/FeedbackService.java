package com.example.feedback.service;

import com.example.feedback.dto.FeedbackRequest;
import com.example.feedback.dto.FeedbackResponse;
import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.Feedback;
import com.example.feedback.entity.FeedbackComponent;
import com.example.feedback.enums.FeedbackStatus;
import com.example.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ApplicationClientService applicationClientService;
    private final FeedbackComponentService feedbackComponentService;

    @Transactional
    public FeedbackResponse create(FeedbackRequest request) {
        ApplicationClient applicationClient = applicationClientService.getEntity(request.applicationClientId());
        FeedbackComponent component = null;

        if (request.componentId() != null) {
            component = feedbackComponentService.getEntity(request.componentId());
            if (!component.getApplicationClient().getId().equals(applicationClient.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le composant ne correspond pas a cette application");
            }
        }

        Feedback feedback = Feedback.builder()
                .applicationClient(applicationClient)
                .component(component)
                .rating(request.rating())
                .comment(request.comment().trim())
                .authorName(defaultAuthorName(request.authorName()))
                .authorEmail(clean(request.authorEmail()))
                .status(FeedbackStatus.PENDING)
                .build();

        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findAll() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(FeedbackResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByApplication(Long applicationClientId) {
        applicationClientService.getEntity(applicationClientId);
        return feedbackRepository.findByApplicationClientIdOrderByCreatedAtDesc(applicationClientId)
                .stream()
                .map(FeedbackResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackResponse findById(Long id) {
        return FeedbackResponse.from(getEntity(id));
    }

    @Transactional
    public FeedbackResponse updateStatus(Long id, FeedbackStatus status) {
        Feedback feedback = getEntity(id);
        feedback.setStatus(status);
        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }

    @Transactional
    public void delete(Long id) {
        Feedback feedback = getEntity(id);
        feedbackRepository.delete(feedback);
    }

    @Transactional(readOnly = true)
    public Feedback getEntity(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feedback introuvable"));
    }

    private String defaultAuthorName(String authorName) {
        String cleaned = clean(authorName);
        return cleaned == null ? "Anonyme" : cleaned;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
