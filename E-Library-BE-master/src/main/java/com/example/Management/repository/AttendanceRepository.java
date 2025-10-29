package com.example.Management.repository;

import com.example.Management.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDate(LocalDate date);
    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    long countByEmployee_IdAndCheckInTimeIsNotNullAndCheckOutTimeIsNotNullAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    //List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    Page<Attendance> findByDate(LocalDate date, Pageable pageable);
    Page<Attendance> findAll(Pageable pageable);

    //List<Attendance> findByEmployee_IdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    Page<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
