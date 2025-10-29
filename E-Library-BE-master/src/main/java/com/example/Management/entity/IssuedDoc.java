package com.example.Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuedDoc {

    @Id
    private String id;
    private Long employeeId;
    private String employeeName;
    private String typeOfDoc;
    private LocalDate dateOfIssue;
    private String issuedBy;
    private String empEmail;
    @Lob
    private byte[] doc;
}