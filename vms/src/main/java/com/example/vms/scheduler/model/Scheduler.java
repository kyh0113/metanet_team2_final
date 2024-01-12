package com.example.vms.scheduler.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Scheduler {
	Integer schedulerId;
	LocalDate workDate;
	String content;
	Integer success;
}
