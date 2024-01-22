package com.example.vms.scheduler.model;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SchedulerResult {
    private int totalRows;
    private int num;
    private int scheduleId;
    private LocalDateTime workDate;
    private String content;
    private int success;
    
    private String successText() {
    	if (success==0) {
    		return "실패";
    	} else if (success==1) {
    		return "성공";
    	} else {
    		return "알 수 없는 결과";
    	}
    }
}
