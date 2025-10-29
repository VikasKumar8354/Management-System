package com.example.Management.repository;


import com.example.Management.entity.POH;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface POHRepository extends JpaRepository<POH, Long> {
    List<POH> findByEmployeeIdAndDateBetweenAndStatus(Long employeeId, LocalDate startDate, LocalDate endDate, String status);

    List<POH> findByEmployeeId(Long employeeId);
    Page<POH> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<POH> findAll(Pageable pageable);
}