package com.example.Management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthSummary {

    private String empName;
    private String empEmail;
    private String role;
    private String monthName;
    private int year;
    private int totalDaysInMonth;
    private int totalWorkingDays;
    private int totalSundays;
    private int totalSaturdays;
    private int totalHolidays;
    private int attendance;
    private int overtimeHours;
    private long leaves;
    private long halfDayLeaves;
    private int presentOnHolidays;
    private int totalAttendance;
}
