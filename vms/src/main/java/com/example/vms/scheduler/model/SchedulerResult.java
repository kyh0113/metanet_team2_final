package com.example.vms.scheduler.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SchedulerResult {
    private int totalRows;
    private int rowNum;
    private int scheduleId;
    private Date workDate;
    private String content;
    private int success;
}
