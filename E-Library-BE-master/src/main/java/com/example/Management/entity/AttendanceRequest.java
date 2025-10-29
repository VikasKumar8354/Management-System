package com.example.Management.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class AttendanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long employeeId;

    private LocalDate date; // Date for which the request is made

    private LocalTime requestedCheckOutTime; // Employee's requested check-out time

    private String status = "Pending"; // Default status: Pending, Approved, Rejected

    private String adminRemarks;

    public void setEmployee(Employee employee) {
    }

    public Long getEmployee() {
        return this.employeeId;
    }
}
