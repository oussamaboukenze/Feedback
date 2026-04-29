package com.example.feedback.controller;

import com.example.feedback.dto.StatisticsResponse;
import com.example.feedback.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    public StatisticsResponse globalStatistics() {
        return statisticsService.globalStatistics();
    }

    @GetMapping("/applications/{applicationClientId}/statistics")
    public StatisticsResponse applicationStatistics(@PathVariable Long applicationClientId) {
        return statisticsService.applicationStatistics(applicationClientId);
    }
}
