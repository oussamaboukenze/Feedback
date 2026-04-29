package com.example.feedback.repository;

import com.example.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findAllByOrderByCreatedAtDesc();

    List<Feedback> findByApplicationClientIdOrderByCreatedAtDesc(Long applicationClientId);

    List<Feedback> findByComponentId(Long componentId);
}
