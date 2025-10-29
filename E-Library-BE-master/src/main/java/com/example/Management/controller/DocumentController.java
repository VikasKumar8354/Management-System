package com.example.Management.controller;

import com.example.Management.dto.DocumentDto;
import com.example.Management.entity.Document;
import com.example.Management.entity.IssuedDoc;
import com.example.Management.service.DocumentService;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam Long employeeId,
            @RequestParam String typeOfDoc,
            @RequestParam("file") MultipartFile file) {

        Document savedDocument = documentService.createDocument(employeeId, typeOfDoc, file);
        return "Document uploaded successfully with ID: " + savedDocument.getId();
    }

//    @GetMapping
//    public List<DocumentDto> getAllDocuments() {
//        return documentService.getAllDocuments();
//    }
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/employee/name/{employeeName}")
    public List<DocumentDto> getDocumentsByEmployeeName(@PathVariable String employeeName) {
        return documentService.getDocumentsByEmployeeName(employeeName);
    }
    // Retrieve a document by employee ID and return its DTO
//    @GetMapping("/employee/{employeeId}")
//    public List<DocumentDto> getDocumentsByEmployeeId(@PathVariable Long employeeId) {
//        List<DocumentDto> documents = documentService.getDocumentsByEmployeeId(employeeId);
//        if (documents.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No documents found for this Employee ID");
//        }
//        return documents;
//    }


    // Retrieve documents by type and return them as DTOs
//    @GetMapping("/type/{typeOfDoc}")
//    public Page<DocumentDto> getDocumentsByType(@PathVariable String typeOfDoc) {
//        return documentService.getDocumentsByType(typeOfDoc);
//    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/download/{id}")
    public void downloadDocument(@PathVariable Long id, HttpServletResponse response) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        // Set response headers for downloading the file
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + document.getTypeOfDoc() + "Document" + id + ".pdf");

        try {
            // Write the file data to the response output stream
            response.getOutputStream().write(document.getDocInBlob());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while downloading file", e);
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/view/{id}")
    public void viewDocument(@PathVariable Long id, HttpServletResponse response) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=document_" + id + ".pdf");
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(document.getDocInBlob());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error while displaying the document", e);
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentDto updateDocument(@PathVariable Long id,
                                      @RequestPart("document") DocumentDto documentDto,
                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        return documentService.updateDocument(id, documentDto, file);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public Page<DocumentDto> getAllDocuments(Pageable pageable) {
        return documentService.getAllDocuments(pageable);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/employee/{employeeId}")
    public Page<DocumentDto> getDocumentsByEmployeeId(@PathVariable Long employeeId, Pageable pageable) {
        return documentService.getDocumentsByEmployeeId(employeeId, pageable);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/type/{typeOfDoc}")
    public Page<DocumentDto> getDocumentsByType(@PathVariable String typeOfDoc, Pageable pageable) {
        return documentService.getDocumentsByType(typeOfDoc, pageable);
    }
}

