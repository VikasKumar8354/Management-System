package com.example.Management.repository;

import com.example.Management.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    long countByEmployeeIdAndLeaveStatusAndIsHalfDay(Long employeeId, String approved, boolean b);
    long countByEmployeeIdAndLeaveStatus(Long employeeId, String approved);

    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<LeaveRequest> findByLeaveDate(LocalDate leaveDate, Pageable pageable);
    Page<LeaveRequest> findByLeaveStatus(String status, Pageable pageable);
}