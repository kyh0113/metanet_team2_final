package com.example.vms.employee.model;

import java.sql.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeVacationCountPerMonthResponseDTO {
	private String startMonth;
	private String endMonth;
	private int startMonthDays;
	private int endMonthDays;
	private Date startDate;
	private Date endDate;
}
