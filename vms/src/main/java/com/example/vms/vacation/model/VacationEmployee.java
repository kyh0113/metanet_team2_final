package com.example.vms.vacation.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class VacationEmployee {

	private int regId;
	private String state;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate regDate;
	private String empName;
}