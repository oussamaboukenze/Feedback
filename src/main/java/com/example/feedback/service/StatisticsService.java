package com.example.feedback.service;

import com.example.feedback.dto.StatisticsResponse;
import com.example.feedback.entity.ApplicationClient;
import com.example.feedback.entity.Feedback;
import com.example.feedback.enums.FeedbackStatus;
import com.example.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final FeedbackRepository feedbackRepository;
    private final ApplicationClientService applicationClientService;

    @Transactional(readOnly = true)
    public StatisticsResponse globalStatistics() {
        return buildStatistics(null, null, feedbackRepository.findAllByOrderByCreatedAtDesc());
    }

    @Transactional(readOnly = true)
    public StatisticsResponse applicationStatistics(Long applicationClientId) {
        ApplicationClient applicationClient = applicationClientService.getEntity(applicationClientId);
        return buildStatistics(
                applicationClient.getId(),
                applicationClient.getName(),
                feedbackRepository.findByApplicationClientIdOrderByCreatedAtDesc(applicationClientId)
        );
    }

    private StatisticsResponse buildStatistics(Long applicationClientId, String applicationClientName, List<Feedback> feedbacks) {
        long total = feedbacks.size();
        double average = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        Map<Integer, Long> distribution = new LinkedHashMap<>();
        IntStream.rangeClosed(1, 5).forEach(rating -> distribution.put(rating, 0L));
        feedbacks.forEach(feedback -> distribution.compute(feedback.getRating(), (rating, count) -> count == null ? 1L : count + 1));

        return new StatisticsResponse(
                applicationClientId,
                applicationClientName,
                total,
                Math.round(average * 100.0) / 100.0,
                countByStatus(feedbacks, FeedbackStatus.PENDING),
                countByStatus(feedbacks, FeedbackStatus.APPROVED),
                countByStatus(feedbacks, FeedbackStatus.REJECTED),
                distribution
        );
    }

    private long countByStatus(List<Feedback> feedbacks, FeedbackStatus status) {
        return feedbacks.stream()
                .filter(feedback -> feedback.getStatus() == status)
                .count();
    }
}
