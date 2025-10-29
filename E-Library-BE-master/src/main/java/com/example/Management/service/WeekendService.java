package com.example.Management.service;


import com.example.Management.entity.WeekendConfig;
import com.example.Management.repository.WeekendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WeekendService {
    @Autowired
    private WeekendRepository weekendRepo;

    public void setWeekends(String week[]) {
        String weekends="";
        for(int i=0;i<week.length;i++){
            if(i==0){
                weekends=weekends+week[i];
            }
            else {
                weekends = weekends + "," + week[i];
            }
        }
        WeekendConfig config = new WeekendConfig();
        config.setWeekendDays(weekends);
        weekendRepo.save(config);
    }

    public Set<DayOfWeek> getWeekends() {
        return weekendRepo.findAll().stream()
                .map(cfg -> cfg.getWeekendDays().split(","))
                .flatMap(Arrays::stream)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }
}