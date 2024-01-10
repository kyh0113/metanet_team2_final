package com.example.vms.schedule.service;

import java.util.List;

import com.example.vms.schedule.model.Schedule;

public interface IScheduleService {

	List<Schedule> getSchedulebydeptId(int dept_id);
	void insertSchedule(Schedule schedule);
	int maxScheduleId();
}
