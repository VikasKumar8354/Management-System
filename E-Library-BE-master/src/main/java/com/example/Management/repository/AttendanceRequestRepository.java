package com.example.Management.repository;



import com.example.Management.entity.AttendanceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRequestRepository extends JpaRepository<AttendanceRequest, Long> {

    List<AttendanceRequest> findByStatus(String status);

    Page<AttendanceRequest> findByEmployeeId(Long employeeId, Pageable pageable);
}
