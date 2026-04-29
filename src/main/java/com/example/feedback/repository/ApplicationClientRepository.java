package com.example.feedback.repository;

import com.example.feedback.entity.ApplicationClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationClientRepository extends JpaRepository<ApplicationClient, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ApplicationClient> findByNameIgnoreCase(String name);

    List<ApplicationClient> findAllByOrderByCreatedAtDesc();
}
