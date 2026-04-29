package com.example.feedback.dto;

import java.util.Map;

public record StatisticsResponse(
        Long applicationClientId,
        String applicationClientName,
        long totalFeedbacks,
        double averageRating,
        long pendingCount,
        long approvedCount,
        long rejectedCount,
        Map<Integer, Long> ratingDistribution
) {
}
