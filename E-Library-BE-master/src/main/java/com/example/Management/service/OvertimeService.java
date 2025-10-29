package com.example.Management.service;



import com.example.Management.entity.Employee;
import com.example.Management.entity.Overtime;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.repository.OvertimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OvertimeService {

    @Autowired
    private OvertimeRepository overtimeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Add Overtime Request
    public Overtime addOvertime(Long employeeId, LocalDate date, Integer hours) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Overtime overtime = new Overtime();
        overtime.setEmployee(employee);
        overtime.setDate(date);
        overtime.setHours(hours);
        overtime.setStatus("Pending"); // Default Status

        return overtimeRepository.save(overtime);
    }

    // Approve or Reject Overtime Request
    public Overtime updateOvertimeStatus(Long overtimeId, String status) {
        Overtime overtime = overtimeRepository.findById(overtimeId)
                .orElseThrow(() -> new RuntimeException("Overtime record not found"));

        if (!status.equalsIgnoreCase("Approved") && !status.equalsIgnoreCase("Rejected")) {
            throw new IllegalArgumentException("Invalid status. Allowed values: Approved, Rejected");
        }

        overtime.setStatus(status);
        return overtimeRepository.save(overtime);
    }

    // Get All Pending Overtime Requests
    public List<Overtime> getPendingOvertimeRequests() {
        return overtimeRepository.findByStatus("Pending");
    }

    // Get Total Overtime Hours for Employee in a Given Month
    public Integer getTotalOvertimeHours(Long employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Overtime> overtimeRecords = overtimeRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
        return overtimeRecords.stream().mapToInt(Overtime::getHours).sum();
    }

//    // Fetch overtime records using employee ID
//    public List<Overtime> getOvertimeByEmployeeId(Long employeeId) {
//        return overtimeRepository.findByEmployeeId(employeeId);
//    }
//
//    public Overtime addOvertime(Long employeeId, LocalDate date, Integer hours) {
//        return overtimeRepository.save(new Overtime(employeeId, date, hours, "Pending"));
//    }

    public Page<Overtime> getPendingOvertimeRequests(int page, int size) {
        return overtimeRepository.findByStatus("Pending", PageRequest.of(page, size));
    }

    public Page<Overtime> getOvertimeByEmployeeId(Long employeeId, int page, int size) {
        return overtimeRepository.findByEmployeeId(employeeId, PageRequest.of(page, size));
    }
}
