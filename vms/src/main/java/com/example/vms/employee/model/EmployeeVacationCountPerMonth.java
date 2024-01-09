package com.example.vms.employee.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeVacationCountPerMonth {
	private String month;
	private int monthPerCount;
}
