package com.example.feedback.controller;

import com.example.feedback.dto.FeedbackComponentRequest;
import com.example.feedback.dto.FeedbackComponentResponse;
import com.example.feedback.service.FeedbackComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackComponentController {

    private final FeedbackComponentService feedbackComponentService;

    @GetMapping("/applications/{applicationClientId}/components")
    public List<FeedbackComponentResponse> findByApplication(@PathVariable Long applicationClientId) {
        return feedbackComponentService.findByApplication(applicationClientId);
    }

    @GetMapping("/components/{id}")
    public FeedbackComponentResponse findById(@PathVariable Long id) {
        return feedbackComponentService.findById(id);
    }

    @PostMapping("/applications/{applicationClientId}/components")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackComponentResponse create(
            @PathVariable Long applicationClientId,
            @Valid @RequestBody FeedbackComponentRequest request
    ) {
        return feedbackComponentService.create(applicationClientId, request);
    }

    @PutMapping("/components/{id}")
    public FeedbackComponentResponse update(@PathVariable Long id, @Valid @RequestBody FeedbackComponentRequest request) {
        return feedbackComponentService.update(id, request);
    }

    @DeleteMapping("/components/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        feedbackComponentService.delete(id);
    }
}
