package com.example.Management.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private String leaveType; // Sick Leave, Casual Leave, Paid Leave
    private String leaveStatus = "Pending"; // Pending, Approved, Rejected
    private boolean isHalfDay;
    private String halfDayType; // First Half or Second Half
    private LocalDate leaveDate = LocalDate.now(); // Date of Leave set automatically

 }