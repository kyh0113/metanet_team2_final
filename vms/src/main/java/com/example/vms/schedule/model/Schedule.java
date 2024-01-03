package com.example.vms.schedule.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Schedule {
	private int calender_Id;
	private String title;
	private Date start_date;
	private Date end_date;
	private String emp_id;
	private int dept_id; 
	private int type_id;
}
