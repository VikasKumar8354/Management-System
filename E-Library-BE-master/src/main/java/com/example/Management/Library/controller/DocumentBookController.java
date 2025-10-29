package com.example.Management.Library.controller;


import com.example.Management.Library.dto.DocumentRequestDTO;
import com.example.Management.Library.dto.DocumentResponseDTO;
import com.example.Management.Library.enums.Department;
import com.example.Management.Library.enums.DocumentCategory;
import com.example.Management.Library.model.DocumentBook;
import com.example.Management.Library.service.DocumentBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documentbooks")
public class DocumentBookController {

    @Autowired
    private DocumentBookService documentService;

    // ✅ Upload document: metadata in JSON, file in multipart
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestPart("metadata") DocumentRequestDTO metadata) throws IOException {

        DocumentResponseDTO response = documentService.uploadDocument(metadata, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<DocumentResponseDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<DocumentResponseDTO>> getDocumentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return ResponseEntity.ok(documentService.getDocumentsPaginated(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }
    @PutMapping("/update-file/{id}")
    public ResponseEntity<DocumentResponseDTO> updateFileOnly(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        DocumentResponseDTO response = documentService.updateFileOnly(id, file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponseDTO>> searchDocuments(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(documentService.searchDocuments(q, pageable));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        DocumentBook document = documentService.getDocumentForDownload(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .body(document.getFileData());
    }

    @GetMapping("/search/by-tags")
    public ResponseEntity<Page<DocumentResponseDTO>> searchDocumentsByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(documentService.searchDocumentsByTags(tags, pageable));
    }




    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date publicationDate,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam("updatedBy") String updatedBy) throws IOException {

        DocumentResponseDTO response = documentService.updateDocument(
                id, title, department, category, publicationDate, tags, file, updatedBy);

        return ResponseEntity.ok(response);
    }



    // *Use this API for fetching the documents*

    @GetMapping("/filter")
    public ResponseEntity<?> filterDocuments(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String categories,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date publicationStartDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date publicationEndDate,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Department deptEnum = null;
        DocumentCategory catEnum = null;

        // Convert department string to enum (case-insensitive)
        if (department != null && !department.isBlank()) {
            try {
                deptEnum = Department.valueOf(department.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid department value: " + department));
            }
        }

        // Convert category string to enum (case-insensitive)
        if (categories != null && !categories.isBlank()) {
            try {
                catEnum = DocumentCategory.valueOf(categories.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid category value: " + categories));
            }
        }

        try {
            Page<DocumentResponseDTO> result = documentService.filterDocuments(
                    deptEnum, catEnum, title, startDate, endDate,
                    publicationStartDate, publicationEndDate, tags, page, size
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error filtering documents: " + e.getMessage()));
        }
    }



}