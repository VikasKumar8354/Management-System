package com.example.Management.service;


import com.example.Management.dto.MonthSummary;
import com.example.Management.entity.Employee;
import com.example.Management.entity.Holiday;
import com.example.Management.entity.Overtime;
import com.example.Management.entity.POH;
import com.example.Management.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonthSummaryService{
    @Autowired
    private WeekendService weekendService;
    @Autowired
    private HolidayService holidayService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private OvertimeRepository overtimeRepository;

    @Autowired
    private POHRepository pohRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private WeekendRepository weekendConfigRepository;



    public MonthSummary getMonthSummary(Long employeeId, int year, int month) {
        System.out.println("Fetching Employee with ID: " + employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Get month name
        String monthName = startDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // Calculate total days in the month
        int totalDaysInMonth = startDate.lengthOfMonth();

        // Count Sundays and Saturdays
        int totalSundays = (int) startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .count();

        int totalSaturdays = (int) startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY)
                .count();

        // Fetch holidays in the month
        List<Holiday> holidays = holidayService.getHolidays(startDate, endDate);
        int totalHolidays = holidays.size();

        // Fetch Present Days
        int presentDays = (int) attendanceRepository.countByEmployee_IdAndCheckInTimeIsNotNullAndCheckOutTimeIsNotNullAndDateBetween(
                employeeId, startDate, endDate);

        // Fetch Overtime Hours
        List<Overtime> overtimeRecords = Optional.ofNullable(
                overtimeRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate)
        ).orElse(List.of());
        int overtimeHours = overtimeRecords.stream().mapToInt(Overtime::getHours).sum();

        // Fetch Approved Leaves
        long approvedLeaves = leaveRequestRepository.countByEmployeeIdAndLeaveStatus(employeeId, "Approved");

        // Fetch Half-Day Leaves
        long halfDayLeaves = Optional.ofNullable(
                leaveRequestRepository.countByEmployeeIdAndLeaveStatusAndIsHalfDay(employeeId, "Approved", true)
        ).orElse(0L);

        // Fetch Present on Holidays (POH)
        List<POH> pohRecords = Optional.ofNullable(
                pohRepository.findByEmployeeIdAndDateBetweenAndStatus(employeeId, startDate, endDate, "Approved")
        ).orElse(List.of());
        int presentOnHolidays = pohRecords.size();

        // Fetch Total Working Days (Excluding Holidays and Weekends)
        Set<DayOfWeek> weekends = weekendService.getWeekends();
        int totalWorkingDays = (int) startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> !weekends.contains(date.getDayOfWeek()))
                .filter(date -> holidays.stream().noneMatch(h -> h.getDate().equals(date)))
                .count();

        // Total Attendance
        int totalAttendance = presentDays + presentOnHolidays;

        // Return the Month Summary
        return new MonthSummary(
                employee.getEmployeeName(),
                employee.getEmail(),
                employee.getRole(),
                monthName,
                year,
                totalDaysInMonth,
                totalWorkingDays,
                totalSundays,
                totalSaturdays,
                totalHolidays,
                presentDays,
                overtimeHours,
                approvedLeaves,
                halfDayLeaves,
                presentOnHolidays,
                totalAttendance
        );
    }




        public List<MonthSummary> getAllEmployeesMonthSummary(int year, int month) {
            List<Employee> employees = employeeRepository.findAll();
            return employees.stream()
                    .map(employee -> getMonthSummary(employee.getId(), year, month))
                    .collect(Collectors.toList());
        }


    // Generate Excel Report
    public ByteArrayOutputStream generateExcelReport(int year, int month) throws IOException {
        List<MonthSummary> summaries = getAllEmployeesMonthSummary(year, month);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Month Summary");

            // Creating Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Employee Name", "Email", "Role", "Month", "Year", "Total Days", "Working Days",
                    "Sundays", "Saturdays", "Holidays", "Present Days", "Overtime Hours",
                    "Approved Leaves", "Half-Day Leaves", "Present on Holidays", "Total Attendance"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // Fill Data
            int rowNum = 1;
            for (MonthSummary summary : summaries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(summary.getEmpName());
                row.createCell(1).setCellValue(summary.getEmpEmail());
                row.createCell(2).setCellValue(summary.getRole());
                row.createCell(3).setCellValue(summary.getMonthName());
                row.createCell(4).setCellValue(summary.getYear());
                row.createCell(5).setCellValue(summary.getTotalDaysInMonth());
                row.createCell(6).setCellValue(summary.getTotalWorkingDays());
                row.createCell(7).setCellValue(summary.getTotalSundays());
                row.createCell(8).setCellValue(summary.getTotalSaturdays());
                row.createCell(9).setCellValue(summary.getTotalHolidays());
                row.createCell(10).setCellValue(summary.getAttendance());
                row.createCell(11).setCellValue(summary.getOvertimeHours());
                row.createCell(12).setCellValue(summary.getLeaves());
                row.createCell(13).setCellValue(summary.getHalfDayLeaves());
                row.createCell(14).setCellValue(summary.getPresentOnHolidays());
                row.createCell(15).setCellValue(summary.getTotalAttendance());
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream;
        }
    }

    // Method to style header cells
    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        return headerCellStyle;
    }



    // Return MonthSummary object


    private Set<DayOfWeek> getWeekendDays() {
        return weekendConfigRepository.findAll().stream()
                .map(cfg -> cfg.getWeekendDays().split(","))
                .flatMap(Arrays::stream)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }
}
