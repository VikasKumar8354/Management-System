package com.example.Management.repository;


import com.example.Management.entity.Overtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public interface OvertimeRepository extends JpaRepository<Overtime, Long> {
    List<Overtime> findByStatus(String status);

    List<Overtime> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    Arrays findByEmployeeIdAndDateBetweenAndStatus(Long employeeId, LocalDate startDate, LocalDate endDate, String approved);

    List<Overtime> findByEmployeeId(Long employeeId);
    Page<Overtime> findByStatus(String status, Pageable pageable);
    Page<Overtime> findByEmployeeId(Long employeeId, Pageable pageable);
}
