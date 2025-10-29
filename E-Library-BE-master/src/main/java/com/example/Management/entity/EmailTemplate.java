package com.example.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String templateType;
    private String templateName;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body; // Template body with placeholders
}


//        "templateName": "Joining Letter",
//        "recipientEmail": "employee@example.com",
//        "Employee Name": "John Doe",
//        "Company Name": "XYZ Pvt Ltd",
//        "Job Title": "Software Engineer",
//        "Joining Date": "10/04/2025",
//        "Reporting Manager’s Name": "Jane Smith",
//        "Reporting Time": "9:00 AM",
//        "Department Name": "IT",
//        "Company Address": "123 Street, City",
//        "HR Contact Name": "Mark Spencer",
//        "HR Contact Email/Phone Number": "hr@xyz.com"