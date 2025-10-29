package com.example.Management.service;

import com.example.Management.entity.Attendance;
import com.example.Management.repository.AttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // Save check-in or check-out
    public String saveAttendance(Attendance attendance) {
        LocalDate today = attendance.getDate();
        Long employeeId = attendance.getEmployee().getId();

        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);
        if (existingAttendance.isPresent()) {
            // Update the existing record (For check-out)
            Attendance existing = existingAttendance.get();
            if (attendance.getCheckOutTime() != null) {
                existing.setCheckOutTime(attendance.getCheckOutTime());
                existing.setStatus("Checked-out");
                attendanceRepository.save(existing);
                return "Check-out successful.";
            }
        }

        // Save new check-in entry if no existing record
        attendanceRepository.save(attendance);
        return "Check-in successful.";
    }

    public Optional<Attendance> getAttendanceByEmployeeAndDate(Long employeeId, LocalDate date) {
        return attendanceRepository.findByEmployeeIdAndDate(employeeId, date);
    }

//    public List<Attendance> getAttendanceByDate(LocalDate date) {
//        return attendanceRepository.findByDate(date);
//    }

    public long getPresentDays(Long employeeId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return attendanceRepository.countByEmployee_IdAndCheckInTimeIsNotNullAndCheckOutTimeIsNotNullAndDateBetween(
                employeeId, startDate, endDate);
    }

//    public List<Attendance> getAttendanceByEmployeeAndMonthYear(Long employeeId, int year, int month) {
//        LocalDate startDate = LocalDate.of(year, month, 1);
//        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
//
//        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
//    }

    public Page<Attendance> getAttendanceByDate(LocalDate date, Pageable pageable) {
        return attendanceRepository.findByDate(date, pageable);
    }

    public Page<Attendance> getAttendanceByEmployeeAndMonthYear(Long employeeId, int year, int month, Pageable pageable) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate, pageable);
    }

    public Page<Attendance> getAllAttendance(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }
}
