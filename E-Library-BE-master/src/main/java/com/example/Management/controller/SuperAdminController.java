package com.example.Management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/superadmin")
public class SuperAdminController {
    @GetMapping("/dashboard")
    public String superAdminDashboard() {
        return "SuperAdmin Dashboard";
    }
}