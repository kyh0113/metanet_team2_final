package com.example.vms.schedule.model;

import java.time.LocalDate;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Schedule {
	private int calender_Id;
	private int reg_id;
	private String title;
	private LocalDate start_date;
	private LocalDate end_date;
	private String emp_id;
	private int dept_id; 
	private int type_id;
}
