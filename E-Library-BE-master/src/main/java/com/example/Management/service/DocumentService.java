package com.example.Management.service;

import com.example.Management.dto.DocumentDto;
import com.example.Management.entity.Document;
import com.example.Management.entity.Employee;
import com.example.Management.repository.DocumentRepository;
import com.example.Management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    //    public Document createDocument(Document document) {
//        return documentRepository.save(document);
//    }
    @Autowired
    private EmployeeRepository employeeRepository; // Inject EmployeeRepository

    public Document createDocument(Long employeeId, String typeOfDoc, MultipartFile file) {
        // Check if the employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with ID: " + employeeId);
        }

        try {
            byte[] fileData = file.getBytes();
            Document document = new Document();
            document.setEmployeeId(employeeId);
            document.setFileName(file.getOriginalFilename()); // Use original filename
            document.setTypeOfDoc(typeOfDoc);
            document.setFileSize(file.getSize());
            document.setContentType(file.getContentType());
            document.setCreatedDate(LocalDate.now());
            document.setDocInBlob(fileData);
            return documentRepository.save(document);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading file", e);
        }
    }

    public List<DocumentDto> getDocumentsByEmployeeName(String employeeName) {
        Employee employee = employeeRepository.findByEmployeeName(employeeName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found with name: " + employeeName));

        List<Document> documents = documentRepository.findByEmployeeId(employee.getId());
        return documents.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setEmployeeId(document.getEmployeeId());
        dto.setFileName(document.getFileName());
        dto.setTypeOfDoc(document.getTypeOfDoc());
        dto.setFileSize(document.getFileSize());
        dto.setContentType(document.getContentType());
        dto.setCreatedDate(document.getCreatedDate());
        return dto;
    }


//    public List<DocumentDto> getAllDocuments() {
//        return documentRepository.findAll()
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

//    public List<DocumentDto> getDocumentsByEmployeeId(Long employeeId) {
//        List<Document> documents = documentRepository.findByEmployeeId(employeeId);
//        return documents.stream().map(this::convertToDto).collect(Collectors.toList());
//    }


//    public List<DocumentDto> getDocumentsByType(String typeOfDoc) {
//        return documentRepository.findByTypeOfDoc(typeOfDoc)
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }


    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }


    public DocumentDto updateDocument(Long id, DocumentDto documentDto, MultipartFile file) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        // Set/update all metadata fields from the DTO.
        document.setEmployeeId(documentDto.getEmployeeId());
        document.setTypeOfDoc(documentDto.getTypeOfDoc());
        document.setCreatedDate(documentDto.getCreatedDate());

        // If no new file is provided, update file-related fields from the DTO.
        if (file == null || file.isEmpty()) {
            document.setFileName(documentDto.getFileName());
            document.setFileSize(documentDto.getFileSize());
            document.setContentType(documentDto.getContentType());
        } else {
            // If a new file is provided, override the file-related properties.
            try {
                document.setDocInBlob(file.getBytes());
                document.setFileName(file.getOriginalFilename());
                document.setFileSize(file.getSize());
                document.setContentType(file.getContentType());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating file", e);
            }
        }
        Document updatedDocument = documentRepository.save(document);
        return convertToDto(updatedDocument);
    }

    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    public Page<DocumentDto> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable).map(this::convertToDto);
    }

    public Page<DocumentDto> getDocumentsByEmployeeId(Long employeeId, Pageable pageable) {
        return documentRepository.findByEmployeeId(employeeId, pageable).map(this::convertToDto);
    }

    public Page<DocumentDto> getDocumentsByType(String typeOfDoc, Pageable pageable) {
        return documentRepository.findByTypeOfDoc(typeOfDoc, pageable).map(this::convertToDto);
    }
}