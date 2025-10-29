package com.example.Management.service;


import com.example.Management.entity.Holiday;
import com.example.Management.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {
    @Autowired
    private HolidayRepository holidayRepo;

    public void addHoliday(Holiday holiday) {
        holidayRepo.save(holiday);
    }

    public void addHolidays(List<Holiday> holidays) {
        holidayRepo.saveAll(holidays);
    }

    public List<Holiday> getHolidays(LocalDate start, LocalDate end) {
        return holidayRepo.findByDateBetween(start, end);
    }
}
