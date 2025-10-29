package com.example.Management.controller;

import com.example.Management.entity.Attendance;
import com.example.Management.entity.Employee;
import com.example.Management.repository.EmployeeRepository;
import com.example.Management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    @Autowired
    private final EmployeeRepository employeeRepository;
    public AttendanceController(AttendanceService attendanceService, EmployeeRepository employeeRepository) {
        this.attendanceService = attendanceService;
        this.employeeRepository = employeeRepository;
    }

    // Check-in API used by Employees

    @PostMapping("/checkin/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    public ResponseEntity<String> checkIn(@PathVariable Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalTime checkInTime = LocalTime.now();

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(optionalEmployee.get()); // Use the persistent entity
        attendance.setDate(today);
        attendance.setCheckInTime(checkInTime);
        attendance.setStatus("Checked-in");

        attendanceService.saveAttendance(attendance);
        return ResponseEntity.ok("Check-in successful at " + checkInTime);
    }

    // Check-out API used by Employees
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @PostMapping("/checkout/{employeeId}")
    public ResponseEntity<String> checkOut(@PathVariable Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalTime checkOutTime = LocalTime.now();

        Optional<Attendance> attendanceOpt = attendanceService.getAttendanceByEmployeeAndDate(employeeId, today);

        if (attendanceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No check-in found for today!");
        }

        Attendance attendance = attendanceOpt.get();

        // Prevent multiple check-outs or check-out without check-in
        if (attendance.getCheckOutTime() != null) {
            return ResponseEntity.badRequest().body("You have already checked out today at " + attendance.getCheckOutTime());
        }

        if (attendance.getCheckInTime() == null) {
            return ResponseEntity.badRequest().body("Check-in record is missing. Please check in first.");
        }

        attendance.setCheckOutTime(checkOutTime);
        attendance.setStatus("Checked-out");
        attendanceService.saveAttendance(attendance);

        return ResponseEntity.ok("Check-out successful at " + checkOutTime);
    }


    // Get present days for employee in a specific month and year
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
    @GetMapping("/present-days/{employeeId}/{year}/{month}")
    public long getPresentDays(@PathVariable Long employeeId,
                               @PathVariable int year,
                               @PathVariable int month) {
        return attendanceService.getPresentDays(employeeId, year, month);
    }

    // Get attendance details for employee by date
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/details/{employeeId}/{date}")
    public ResponseEntity<Optional<Attendance>> getAttendanceByEmployeeAndDate(
            @PathVariable Long employeeId,
            @PathVariable String date) {
        Optional<Attendance> attendance = attendanceService.getAttendanceByEmployeeAndDate(employeeId, LocalDate.parse(date));
        return ResponseEntity.ok(attendance);
    }

    // Get all attendance for a specific date (pagination)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/{date}")
    public Page<Attendance> getAttendance(@PathVariable String date,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return attendanceService.getAttendanceByDate(LocalDate.parse(date), pageable);
    }

    // Get attendance by employee ID, year, and month (pagination)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/logs/{employeeId}/{year}/{month}")
    public Page<Attendance> getAttendanceByEmployeeAndMonthYear(@PathVariable Long employeeId,
                                                                @PathVariable int year,
                                                                @PathVariable int month,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return attendanceService.getAttendanceByEmployeeAndMonthYear(employeeId, year, month, pageable);
    }

    // Get all attendance with pagination
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/all")
    public Page<Attendance> getAllAttendance(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return attendanceService.getAllAttendance(pageable);
    }
}
