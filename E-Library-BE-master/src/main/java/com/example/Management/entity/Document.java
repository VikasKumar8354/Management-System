package com.example.Management.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String fileName;
    private String typeOfDoc;
    private long fileSize;
    private String contentType;
    @Lob
    private byte[] docInBlob;
    private LocalDate createdDate;
}

