package com.example.Management.controller;


import com.example.Management.entity.LeaveRequest;
import com.example.Management.repository.LeaveRequestRepository;
import com.example.Management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
class LeaveRequestController {
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestService service;

    public LeaveRequestController(LeaveRequestService service) {
        this.service = service;
    }

    // Apply for leave (Employees)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/apply")
    public LeaveRequest applyLeave(@RequestBody LeaveRequest leaveRequest) {
        return service.applyLeave(leaveRequest);
    }

    // Fetch leaves by employee with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/get/{employeeId}")
    public ResponseEntity<Page<LeaveRequest>> getLeavesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveRequest> leaveRequests = service.getLeavesByEmployee(employeeId, pageable);
        return ResponseEntity.ok(leaveRequests);
    }

    // Fetch leaves by date with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/date/{leaveDate}")
    public ResponseEntity<Page<LeaveRequest>> getLeavesByDate(
            @PathVariable String leaveDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveRequest> leaveRequests = service.getLeavesByDate(LocalDate.parse(leaveDate), pageable);
        return ResponseEntity.ok(leaveRequests);
    }

    // Fetch pending leaves with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/pending")
    public ResponseEntity<Page<LeaveRequest>> getPendingLeaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LeaveRequest> pendingLeaves = service.getPendingLeaves(pageable);
        return ResponseEntity.ok(pendingLeaves);
    }

    // Approve or reject leave (Admin)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/approve-reject/{leaveId}")
    public LeaveRequest approveOrRejectLeave(@PathVariable Long leaveId,
                                             @RequestParam Long managerId,
                                             @RequestParam String status) {
        return service.approveOrRejectLeave(leaveId, managerId, status);
    }

    // Cancel leave request (Employee)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/cancel/{leaveId}")
    public String cancelLeave(@PathVariable Long leaveId, @RequestParam Long employeeId) {
        service.cancelLeave(leaveId, employeeId);
        return "Leave request cancelled successfully";
    }

    // Get approved leave counts
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/approved-leave-counts/{employeeId}")
    public Map<String, Long> getApprovedLeaveCounts(@PathVariable Long employeeId) {
        return service.getLeaveCounts(employeeId);
    }

    // Fetch all leave request records with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/all")
    public Page<LeaveRequest> getAllLeaveRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return leaveRequestRepository.findAll(PageRequest.of(page, size));
    }
}
