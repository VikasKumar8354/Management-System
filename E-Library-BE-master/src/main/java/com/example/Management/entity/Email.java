package com.example.Management.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Email {


    @Id
    private String id;
    private String lettertype;
    private String employeeName;
    private LocalDateTime dateTime;
    private String recipientEmail;
    @Column(name = "email_body", columnDefinition = "LONGTEXT")
    private String emailBody;
    private String templateName;
}