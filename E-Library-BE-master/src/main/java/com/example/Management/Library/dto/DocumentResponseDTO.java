package com.example.Management.Library.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DocumentResponseDTO {

    private Long id;
    private String title;
    private String department;
    private String categories;
    private Date publicationDate;
    private List<String> tags;
    private boolean isActiveVersion;
    private String downloadLink;
}