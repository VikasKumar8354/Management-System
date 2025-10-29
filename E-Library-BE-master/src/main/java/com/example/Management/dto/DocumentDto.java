package com.example.Management.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    private Long id;

    private Long employeeId;
    private String fileName;
    private String typeOfDoc;
    private long fileSize;
    private String contentType;
    private LocalDate createdDate;

}
