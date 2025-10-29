package com.example.Management.controller;


import com.example.Management.service.WeekendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.Set;

@RestController
@RequestMapping("/api/weekends")
public class WeekendController {
    @Autowired
    private WeekendService weekendService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public void setWeekends(@RequestBody String week[]) {
        weekendService.setWeekends(week);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public Set<DayOfWeek> getWeekends() {
        return weekendService.getWeekends();
    }
}