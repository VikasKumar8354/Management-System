package com.example.Management.repository;


import com.example.Management.entity.WeekendConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeekendRepository extends JpaRepository<WeekendConfig, Long> {
    Optional<Object> findTopByOrderByIdDesc();
}