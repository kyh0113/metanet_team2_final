package com.example.vms.scheduler.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {
	
	@Scheduled(cron = "*/20 * * * * *") 
    public void second100() {
        System.out.println("안녕 히원아?");
    } 
}
