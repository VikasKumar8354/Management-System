package com.example.Management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class WeekendConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String weekendDays;

    public void setWeekendDays(String weekendDays) {
        this.weekendDays = weekendDays.trim().replace("\"", ""); // Clean before saving
    }
}
