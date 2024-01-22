package com.example.vms.scheduler.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Scheduler {
	Integer schedulerId;
	LocalDateTime workDate;
	String content;
	Integer success;
}
