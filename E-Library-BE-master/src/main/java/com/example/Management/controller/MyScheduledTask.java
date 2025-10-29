package com.example.Management.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyScheduledTask {

    @Scheduled(fixedRate = 5000)
//    public void runEveryFiveSeconds() {
//        System.out.println("Running every 5 seconds - " + System.currentTimeMillis());
//    }

    @Scheduled(fixedDelay = 10000)
    public void runWithFixedDelay() {
//        System.out.println("Running with 10 seconds delay - " + System.currentTimeMillis());
    }

    // Runs at 10:00 AM every day (CRON format: sec min hour day month day-of-week)
    @Scheduled(cron = "0 0 10 * * ?")
    public void runDailyAtTen() {
//        System.out.println("Running daily at 10 AM - " + System.currentTimeMillis());
    }
}