package com.example.Management.controller;


import com.example.Management.service.WorkingDaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/working-days")
public class WorkingDaysController {
    @Autowired
    private WorkingDaysService workingDaysService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{year}/{month}")
    public long getWorkingDays(@PathVariable int year, @PathVariable int month) {
        return workingDaysService.calculateWorkingDays(year, month);
    }
}