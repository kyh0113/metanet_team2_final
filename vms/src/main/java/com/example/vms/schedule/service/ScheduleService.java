package com.example.vms.schedule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.repository.IScheduleRepository;

@Service
public class ScheduleService implements IScheduleService{
	
	@Autowired
	IScheduleRepository iScheduleRepository;

	@Override
	public List<Schedule> getSchedulebydeptId(int dept_id) {
		return iScheduleRepository.getSchedulebydeptId(dept_id);
	}

	@Override
	public void insertSchedule(Schedule schedule) {
		iScheduleRepository.insertSchedule(schedule);
	}

	@Override
	public int maxScheduleId() {
		return iScheduleRepository.maxScheduleId();
	}
	

}
