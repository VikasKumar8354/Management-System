package com.example.Management.Library.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class DocumentRequestDTO {


    private String title;
    private String department;
    private String category;
    private Date publicationDate;

    // Accepts a list of tags directly
    private List<String> tags;
}