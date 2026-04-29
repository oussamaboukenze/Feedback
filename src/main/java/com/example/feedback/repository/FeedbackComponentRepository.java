package com.example.feedback.repository;

import com.example.feedback.entity.FeedbackComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackComponentRepository extends JpaRepository<FeedbackComponent, Long> {

    boolean existsByApplicationClientIdAndNameIgnoreCase(Long applicationClientId, String name);

    List<FeedbackComponent> findByApplicationClientIdOrderByNameAsc(Long applicationClientId);
}
