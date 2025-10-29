package com.example.Management.service;


import com.example.Management.entity.Employee;
import com.example.Management.entity.LeaveRequest;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.repository.LeaveRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class LeaveRequestService {
    private final LeaveRequestRepository repository;
    private final EmployeeRepository employeeRepository;

    public LeaveRequestService(LeaveRequestRepository repository, EmployeeRepository employeeRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
    }

    public LeaveRequest applyLeave(LeaveRequest leaveRequest) {
        leaveRequest.setLeaveDate(LocalDate.now()); // Auto-set leave date
        return repository.save(leaveRequest);
    }

    public Page<LeaveRequest> getLeavesByEmployee(Long employeeId, Pageable pageable) {
        return repository.findByEmployeeId(employeeId, pageable);
    }

    public Page<LeaveRequest> getLeavesByDate(LocalDate leaveDate, Pageable pageable) {
        return repository.findByLeaveDate(leaveDate, pageable);
    }

    public Page<LeaveRequest> getPendingLeaves(Pageable pageable) {
        return repository.findByLeaveStatus("Pending", pageable);
    }

    public LeaveRequest approveOrRejectLeave(Long leaveId, Long managerId, String status) {
        Employee manager = employeeRepository.findByIdAndRole(managerId, "Manager");
        if (manager == null) {
            throw new RuntimeException("Only managers can approve or reject leave requests.");
        }
        LeaveRequest leaveRequest = repository.findById(leaveId).orElseThrow();
        leaveRequest.setLeaveStatus(status);
        return repository.save(leaveRequest);
    }

    public void cancelLeave(Long leaveId, Long employeeId) {
        LeaveRequest leaveRequest = repository.findById(leaveId).orElseThrow();
        if (!leaveRequest.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Employees can only cancel their own leave requests.");
        }
        if (!leaveRequest.getLeaveStatus().equals("Pending")) {
            throw new RuntimeException("Leave request cannot be cancelled after approval or rejection.");
        }
        repository.delete(leaveRequest);
    }

    public Map<String, Long> getLeaveCounts(Long employeeId) {
        long approvedLeaves = repository.countByEmployeeIdAndLeaveStatus(employeeId, "Approved");
        long approvedHalfDayLeaves = repository.countByEmployeeIdAndLeaveStatusAndIsHalfDay(employeeId, "Approved", true);

        Map<String, Long> leaveCounts = new HashMap<>();
        leaveCounts.put("approvedLeaves", approvedLeaves);
        leaveCounts.put("approvedHalfDayLeaves", approvedHalfDayLeaves);

        return leaveCounts;
    }
}
