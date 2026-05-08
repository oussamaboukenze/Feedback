package com.example.feedback.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.feedback.entity.ApplicationClient;

@Repository
public interface ApplicationClientRepository extends JpaRepository<ApplicationClient, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ApplicationClient> findByNameIgnoreCase(String name);

    List<ApplicationClient> findAllByOrderByCreatedAtDesc();

    List<ApplicationClient> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    List<ApplicationClient> findAllByActiveOrderByNameAsc(boolean active);
}
