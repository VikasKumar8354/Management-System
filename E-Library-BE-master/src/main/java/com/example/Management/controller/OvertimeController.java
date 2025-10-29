package com.example.Management.controller;



import com.example.Management.entity.Overtime;
import com.example.Management.repository.OvertimeRepository;
import com.example.Management.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    @Autowired
    private OvertimeService overtimeService;
    @Autowired
    private OvertimeRepository overtimeRepository;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping("/add")
    public Overtime addOvertime(@RequestParam Long employeeId,
                                @RequestParam LocalDate date,
                                @RequestParam Integer hours) {
        return overtimeService.addOvertime(employeeId, date, hours);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/all")
    public Page<Overtime> getAllOvertimeRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return overtimeRepository.findAll(PageRequest.of(page, size));
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @PutMapping("/{overtimeId}/approve")
    public Overtime approveOvertime(@PathVariable Long overtimeId) {
        return overtimeService.updateOvertimeStatus(overtimeId, "Approved");
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @PutMapping("/{overtimeId}/reject")
    public Overtime rejectOvertime(@PathVariable Long overtimeId) {
        return overtimeService.updateOvertimeStatus(overtimeId, "Rejected");
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/pending")
    public Page<Overtime> getPendingOvertimeRequests(@RequestParam int page, @RequestParam int size) {
        return overtimeService.getPendingOvertimeRequests(page, size);
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/employee/{employeeId}")
    public Page<Overtime> getOvertimeByEmployeeId(@PathVariable Long employeeId,
                                                  @RequestParam int page,
                                                  @RequestParam int size) {
        return overtimeService.getOvertimeByEmployeeId(employeeId, page, size);
    }
}
