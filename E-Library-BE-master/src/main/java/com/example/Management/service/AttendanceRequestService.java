package com.example.Management.service;


import com.example.Management.entity.Attendance;
import com.example.Management.entity.AttendanceRequest;
import com.example.Management.repository.AttendanceRepository;
import com.example.Management.repository.AttendanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AttendanceRequestService {
    private final AttendanceRequestRepository attendanceRequestRepository;
    private final AttendanceRepository attendanceRepository;

    public AttendanceRequest createRequest(Long employeeId, String date, String requestedTime) {
        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = LocalTime.parse(requestedTime);

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, localDate)
                .orElseThrow(() -> new RuntimeException("No attendance record found for the given date"));

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Check-out already exists for this date.");
        }

        AttendanceRequest request = new AttendanceRequest();
        request.setEmployeeId(employeeId);
        request.setDate(localDate);
        request.setRequestedCheckOutTime(localTime);
        request.setStatus("Pending");

        return attendanceRequestRepository.save(request);
    }

    public AttendanceRequest approveRequest(Long requestId, String adminRemarks) {
        AttendanceRequest request = attendanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"Pending".equals(request.getStatus())) {
            throw new RuntimeException("Request is already processed");
        }

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getDate())
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        attendance.setCheckOutTime(request.getRequestedCheckOutTime());
        attendance.setStatus("CheckedOut");
        attendanceRepository.save(attendance);

        request.setStatus("Approved");
        request.setAdminRemarks(adminRemarks);
        return attendanceRequestRepository.save(request);
    }

    public AttendanceRequest rejectRequest(Long requestId, String adminRemarks) {
        AttendanceRequest request = attendanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"Pending".equals(request.getStatus())) {
            throw new RuntimeException("Request is already processed");
        }

        request.setStatus("Rejected");
        request.setAdminRemarks(adminRemarks);
        return attendanceRequestRepository.save(request);
    }

    public Page<AttendanceRequest> getRequestsByEmployeeId(Long employeeId, Pageable pageable) {
        return attendanceRequestRepository.findByEmployeeId(employeeId, pageable);
    }

    public Page<AttendanceRequest> getAllAttendanceRequests(Pageable pageable) {
        return attendanceRequestRepository.findAll(pageable);
    }
}
