package com.example.Management.controller;


import com.example.Management.entity.Holiday;
import com.example.Management.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {
    @Autowired
    private HolidayService holidayService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public void addHoliday(@RequestBody Holiday holiday) {
        holidayService.addHoliday(holiday);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/bulk")
    public void addHolidays(@RequestBody List<Holiday> holidays) {
        holidayService.addHolidays(holidays);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{year}/{month}")
    public List<Holiday> getHolidays(@PathVariable int year, @PathVariable int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return holidayService.getHolidays(start, end);
    }
}
