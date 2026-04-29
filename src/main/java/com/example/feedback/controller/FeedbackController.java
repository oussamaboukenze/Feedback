package com.example.feedback.controller;

import com.example.feedback.dto.FeedbackRequest;
import com.example.feedback.dto.FeedbackResponse;
import com.example.feedback.dto.FeedbackStatusRequest;
import com.example.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/feedbacks")
    public List<FeedbackResponse> findAll() {
        return feedbackService.findAll();
    }

    @GetMapping("/feedbacks/{id}")
    public FeedbackResponse findById(@PathVariable Long id) {
        return feedbackService.findById(id);
    }

    @GetMapping("/applications/{applicationClientId}/feedbacks")
    public List<FeedbackResponse> findByApplication(@PathVariable Long applicationClientId) {
        return feedbackService.findByApplication(applicationClientId);
    }

    @PostMapping("/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse create(@Valid @RequestBody FeedbackRequest request) {
        return feedbackService.create(request);
    }

    @PatchMapping("/feedbacks/{id}/status")
    public FeedbackResponse updateStatus(@PathVariable Long id, @Valid @RequestBody FeedbackStatusRequest request) {
        return feedbackService.updateStatus(id, request.status());
    }

    @DeleteMapping("/feedbacks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        feedbackService.delete(id);
    }
}
