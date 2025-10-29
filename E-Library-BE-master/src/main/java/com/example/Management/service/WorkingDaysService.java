package com.example.Management.service;


import com.example.Management.entity.Holiday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class WorkingDaysService {
    @Autowired
    private HolidayService holidayService;
    @Autowired private WeekendService weekendService;

    public long calculateWorkingDays(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Set<DayOfWeek> weekends = weekendService.getWeekends();
        List<Holiday> holidays = holidayService.getHolidays(start, end);

        return start.datesUntil(end.plusDays(1))
                .filter(date -> !weekends.contains(date.getDayOfWeek()))
                .filter(date -> holidays.stream().noneMatch(h -> h.getDate().equals(date)))
                .count();
    }
}
