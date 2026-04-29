package com.example.feedback.controller;

import com.example.feedback.dto.ApplicationClientRequest;
import com.example.feedback.dto.ApplicationClientResponse;
import com.example.feedback.service.ApplicationClientService;
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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationClientController {

    private final ApplicationClientService applicationClientService;

    @GetMapping
    public List<ApplicationClientResponse> findAll() {
        return applicationClientService.findAll();
    }

    @GetMapping("/{id}")
    public ApplicationClientResponse findById(@PathVariable Long id) {
        return applicationClientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationClientResponse create(@Valid @RequestBody ApplicationClientRequest request) {
        return applicationClientService.create(request);
    }

    @PutMapping("/{id}")
    public ApplicationClientResponse update(@PathVariable Long id, @Valid @RequestBody ApplicationClientRequest request) {
        return applicationClientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applicationClientService.delete(id);
    }
}
