package com.example.Management.service;


import com.example.Management.dto.IssuedDocDTO;
import com.example.Management.entity.Employee;

import com.example.Management.entity.IssuedDoc;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.repository.IssuedDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class IssuedDocService {

    @Autowired
    private IssuedDocRepository issuedDocRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Create and save a new issued document
    public IssuedDoc createIssuedDoc(Long employeeId, String employeeName, String typeOfDoc, String issuedBy, String empEmail, MultipartFile file) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new RuntimeException("Employee with ID " + employeeId + " does not exist.");
        }

        IssuedDoc issuedDoc = new IssuedDoc();
        issuedDoc.setId(generateIssuedDocId());
        issuedDoc.setEmployeeName(employeeName);
        issuedDoc.setEmployeeId(employeeId);
        issuedDoc.setTypeOfDoc(typeOfDoc);
        issuedDoc.setEmpEmail(empEmail);
        issuedDoc.setIssuedBy(issuedBy);
        issuedDoc.setDateOfIssue(LocalDate.now());

        try {
            issuedDoc.setDoc(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file", e);
        }

        return issuedDocRepository.save(issuedDoc);
    }

    // Generate unique document ID like 2025APR0DOC0001
    public String generateIssuedDocId() {
        LocalDate today = LocalDate.now();
        String prefix = today.getYear() +
                today.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase() +
                "0DOC";

        List<IssuedDoc> allDocs = issuedDocRepository.findAll();

        int max = allDocs.stream()
                .filter(doc -> doc.getId() != null && doc.getId().startsWith(prefix))
                .mapToInt(doc -> {
                    try {
                        return Integer.parseInt(doc.getId().substring(prefix.length()));
                    } catch (Exception e) {
                        return 0;
                    }
                }).max().orElse(0);

        return prefix + String.format("%04d", max + 1);
    }

    // Get all issued documents with pagination and DTO mapping
    public Page<IssuedDocDTO> getAllIssuedDocs(Pageable pageable) {
        return issuedDocRepository.findAll(pageable).map(doc -> {
            Optional<Employee> empOpt = employeeRepository.findById(doc.getEmployeeId());

            return new IssuedDocDTO(
                    doc.getId(),
                    doc.getEmployeeId(),
                    empOpt.map(Employee::getEmployeeName).orElse("N/A"),
                    empOpt.map(Employee::getRole).orElse("N/A"),
                    doc.getTypeOfDoc(),
                    doc.getDateOfIssue(),
                    doc.getEmpEmail(),
                    doc.getIssuedBy(),
                    doc.getDoc()
            );
        });
    }

    public Optional<IssuedDoc> getIssuedDocById(String id) {
        return issuedDocRepository.findById(id);
    }

    public Optional<IssuedDoc> getIssuedDocByEmployeeId(Long employeeId) {
        return issuedDocRepository.findByEmployeeId(employeeId);
    }

    // Update an existing issued document
    public IssuedDoc updateIssuedDoc(String id,String employeeName,String empEmail, Long employeeId, String typeOfDoc, String issuedBy,
                                     LocalDate dateOfIssue, MultipartFile file) {
        IssuedDoc doc = (IssuedDoc) issuedDocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        if (employeeId != null) doc.setEmployeeId(employeeId);
        if (employeeName != null) doc.setEmployeeName(employeeName);
        if(empEmail != null) doc.setEmpEmail(empEmail);
        if (typeOfDoc != null) doc.setTypeOfDoc(typeOfDoc);
        if (issuedBy != null) doc.setIssuedBy(issuedBy);
        if (dateOfIssue != null) doc.setDateOfIssue(dateOfIssue);

        if (file != null && !file.isEmpty()) {
            try {
                doc.setDoc(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read file data", e);
            }
        }

        return issuedDocRepository.save(doc);
    }

    public void deleteIssuedDoc(String id) {
        IssuedDoc doc = issuedDocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        issuedDocRepository.delete(doc);
    }

    // Filters
    public Page<IssuedDoc> getIssuedDocsByEmployeeId(Long employeeId, Pageable pageable) {
        return issuedDocRepository.findByEmployeeId(employeeId, pageable);
    }

    public Page<IssuedDoc> getIssuedDocsByDate(LocalDate dateOfIssue, Pageable pageable) {
        return issuedDocRepository.findByDateOfIssue(dateOfIssue, pageable);
    }

    public Page<IssuedDoc> getIssuedDocsByTypeOfDoc(String typeOfDoc, Pageable pageable) {
        return issuedDocRepository.findByTypeOfDoc(typeOfDoc, pageable);
    }

    public Page<IssuedDoc> getIssuedDocsByIssuedBy(String issuedBy, Pageable pageable) {
        return issuedDocRepository.findByIssuedBy(issuedBy, pageable);
    }

    public Page<IssuedDoc> getIssuedDocsByEmpEmail(String empEmail, Pageable pageable) {
        return issuedDocRepository.findByEmpEmail(empEmail, pageable);
    }

}