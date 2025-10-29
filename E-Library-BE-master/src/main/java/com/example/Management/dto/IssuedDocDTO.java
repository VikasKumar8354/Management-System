package com.example.Management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuedDocDTO {
    private String id;
    private Long employeeId;
    private String employeeName; // Added field
    private String role; // Added field
    private String typeOfDoc;
    private LocalDate dateOfIssue;
    private String empEmail;
    private String issuedBy;
    private byte[] doc;


}