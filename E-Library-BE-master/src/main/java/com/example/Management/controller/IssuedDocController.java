package com.example.Management.controller;

import com.example.Management.dto.IssuedDocDTO;
import com.example.Management.entity.Employee;
import com.example.Management.entity.IssuedDoc;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.repository.IssuedDocRepository;
import com.example.Management.service.IssuedDocService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/issued-docs")
public class IssuedDocController {

    @Autowired
    private IssuedDocService issuedDocService;
    @Autowired
    private IssuedDocRepository issuedDocRepository;
    @Autowired
    private EmployeeRepository employeeRepository;


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public IssuedDoc createIssuedDoc(
            @RequestParam Long employeeId,
            @RequestParam String typeOfDoc,
            @RequestParam String employeeName,
            @RequestParam String issuedBy,
            @RequestParam String empEmail,
            @RequestParam("file") MultipartFile file
    ) {
        return issuedDocService.createIssuedDoc(employeeId,employeeName, typeOfDoc, issuedBy, empEmail, file);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}")
    public IssuedDoc updateIssuedDoc(
            @PathVariable String id,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String typeOfDoc,
            @RequestParam(required = false) String issuedBy,
            @RequestParam(required = false) String empEmail,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfIssue,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        return issuedDocService.updateIssuedDoc(id,employeeName,empEmail, employeeId, typeOfDoc, issuedBy, dateOfIssue, file);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteIssuedDoc(@PathVariable String id) {
        issuedDocService.deleteIssuedDoc(id);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/download/{id}")
    public void downloadIssuedDoc(@PathVariable String id, HttpServletResponse response) {
        IssuedDoc issuedDoc = (IssuedDoc) issuedDocService.getIssuedDocById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=document_" + id + ".pdf");  // Adjust extension as needed

        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(issuedDoc.getDoc());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error while downloading the document", e);
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/view/{id}")
    public void viewIssuedDoc(@PathVariable String id, HttpServletResponse response) {
        IssuedDoc issuedDoc = (IssuedDoc) issuedDocService.getIssuedDocById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=document_" + id + ".pdf");
        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(issuedDoc.getDoc());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error while displaying the document", e);
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/getall")
    public Page<IssuedDocDTO> getAllIssuedDocs(Pageable pageable) {
        Page<IssuedDoc> issuedDocs = issuedDocRepository.findAll(pageable);

        return issuedDocs.map(doc -> {
            Optional<Employee> employee = (doc.getEmployeeId() != null) ?
                    employeeRepository.findById(doc.getEmployeeId()) :
                    Optional.empty();

            return new IssuedDocDTO(
                    doc.getId(),
                    doc.getEmployeeId(), // can be null, that's okay
                    doc.getEmployeeName(),
                    employee.map(Employee::getRole).orElse("N/A"),
                    doc.getTypeOfDoc(),
                    doc.getDateOfIssue(),
                    doc.getEmpEmail(),
                    doc.getIssuedBy() != null ? doc.getIssuedBy() : "N/A",
                    doc.getDoc()
            );
        });
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/employee/{employeeId}")
    public Page<IssuedDoc> getIssuedDocsByEmployeeId(@PathVariable Long employeeId, Pageable pageable) {
        return issuedDocService.getIssuedDocsByEmployeeId(employeeId, pageable);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/by-date")
    public Page<IssuedDoc> getIssuedDocsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfIssue,
            Pageable pageable) {
        return issuedDocService.getIssuedDocsByDate(dateOfIssue, pageable);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/by-type")
    public Page<IssuedDoc> getIssuedDocsByTypeOfDoc(@RequestParam String typeOfDoc, Pageable pageable) {
        return issuedDocService.getIssuedDocsByTypeOfDoc(typeOfDoc, pageable);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/by-issuedby")
    public Page<IssuedDoc> getIssuedDocsByIssuedBy(@RequestParam String issuedBy, Pageable pageable) {
        return issuedDocService.getIssuedDocsByIssuedBy(issuedBy, pageable);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/by-email")
    public Page<IssuedDoc> getIssuedDocsByEmail(@RequestParam String empEmail, Pageable pageable) {
        return issuedDocService.getIssuedDocsByEmpEmail(empEmail, pageable);
    }
}