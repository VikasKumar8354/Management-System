package com.example.Management.dto;

import com.example.Management.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long employeeId;
    private String employeeName;
    private String address;
    private Long phone;
    private String email;
    private String role;

    // A list of Document DTOs
    private List<DocumentDto> documentDtos;
}
