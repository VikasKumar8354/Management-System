package com.example.Management.Library.model;

import com.example.Management.Library.enums.Department;
import com.example.Management.Library.enums.DocumentCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class DocumentBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Enumerated(EnumType.STRING)
    private DocumentCategory categories;

    private Date publicationDate;

    @ElementCollection
    private List<String> tags;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private boolean isActiveVersion = true;

    private String fileName;
    private String fileType;

    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
}
