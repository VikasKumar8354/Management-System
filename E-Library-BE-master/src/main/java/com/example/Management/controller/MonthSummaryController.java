package com.example.Management.controller;


import com.example.Management.dto.MonthSummary;
import com.example.Management.service.MonthSummaryService;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/month")
public class MonthSummaryController {

    @Autowired
    private MonthSummaryService monthSummaryService;

    // Get month summary for a specific employee
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/summary")
    public MonthSummary getMonthSummary(
            @RequestParam Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return monthSummaryService.getMonthSummary(employeeId, year, month);
    }

    // Get month summary for all employees
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/summary/all")
    public List<MonthSummary> getAllEmployeesMonthSummary(
            @RequestParam int year,
            @RequestParam int month) {
        return monthSummaryService.getAllEmployeesMonthSummary(year, month);
    }

    // API to generate an Excel report
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/summary/excel")
    public ResponseEntity<byte[]> generateExcelReport(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            ByteArrayOutputStream excelStream = monthSummaryService.generateExcelReport(year, month);
            byte[] excelBytes = excelStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Month_Summary_" + month + "_" + year + ".xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}