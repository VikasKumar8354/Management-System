package com.example.Management.controller;


import com.example.Management.entity.AttendanceRequest;
import com.example.Management.service.AttendanceRequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance-requests")
@RequiredArgsConstructor
public class AttendanceRequestController {
    private final AttendanceRequestService attendanceRequestService;

    // Raise request for missed punch-out (Employee)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @PostMapping("/create")
    public ResponseEntity<AttendanceRequest> createRequest(@RequestParam Long employeeId,
                                                           @RequestParam String date,
                                                           @RequestParam String requestedTime) {
        AttendanceRequest request = attendanceRequestService.createRequest(employeeId, date, requestedTime);
        return ResponseEntity.ok(request);
    }

    // Approve request (Admin)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<AttendanceRequest> approveRequest(@PathVariable Long requestId,
                                                            @RequestParam String adminRemarks) {
        AttendanceRequest request = attendanceRequestService.approveRequest(requestId, adminRemarks);
        return ResponseEntity.ok(request);
    }

    // Reject request (Admin)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<AttendanceRequest> rejectRequest(@PathVariable Long requestId,
                                                           @RequestParam String adminRemarks) {
        AttendanceRequest request = attendanceRequestService.rejectRequest(requestId, adminRemarks);
        return ResponseEntity.ok(request);
    }

    // Fetch requests by employee with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<AttendanceRequest>> getRequestsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceRequest> requests = attendanceRequestService.getRequestsByEmployeeId(employeeId, pageable);
        return ResponseEntity.ok(requests);
    }

    // Fetch all attendance requests with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<AttendanceRequest>> getAllAttendanceRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AttendanceRequest> requests = attendanceRequestService.getAllAttendanceRequests(pageable);
        return ResponseEntity.ok(requests);
    }
}
