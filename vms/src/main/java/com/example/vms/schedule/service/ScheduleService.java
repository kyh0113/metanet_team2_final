package com.example.vms.schedule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.schedule.repository.IScheduleRepository;

@Service
public class ScheduleService implements IScheduleService{
	
	@Autowired
	IScheduleRepository iScheduleRepository;
	
	@Autowired
    public ScheduleService(IScheduleRepository iScheduleRepository) {
        this.iScheduleRepository = iScheduleRepository;
    }

	@Override
	public List<Schedule> getSchedulebydeptId(int dept_id) {
		return iScheduleRepository.getSchedulebydeptId(dept_id);
	}

	@Override
	@Transactional
	public void insertSchedule(Schedule schedule) {
		iScheduleRepository.insertSchedule(schedule);
	}

	@Override
	public int maxScheduleId() {
		return iScheduleRepository.maxScheduleId();
	}

	@Override
	public int getCountAllSchedule() {
		return iScheduleRepository.getCountAllSchedule();
	}

	@Override
	public List<ScheduleEmpDeptType> getAllScheduleList(int startNum, int endNum) {
		return iScheduleRepository.getAllScheduleList(startNum, endNum);
	}

	@Override
	public int getCountScheduleByEmpName(String empName) {
		return iScheduleRepository.getCountScheduleByEmpName(empName);
	}

	@Override
	public List<ScheduleEmpDeptType> getScheduleListByEmpName(int startNum, int endNum, String empName) {
		return iScheduleRepository.getScheduleListByEmpName(startNum, endNum, empName);
	}

	@Override
	public int getCountScheduleByDeptName(String deptName) {
		return iScheduleRepository.getCountScheduleByDeptName(deptName);
	}

	@Override
	public List<ScheduleEmpDeptType> getScheduleListByDeptName(int startNum, int endNum, String deptName) {
		return iScheduleRepository.getScheduleListByDeptName(startNum, endNum, deptName);
	}

	@Override
	public int getCountScheduleByPosition(String position) {
		return iScheduleRepository.getCountScheduleByPosition(position);
	}

	@Override
	public List<ScheduleEmpDeptType> getScheduleListByPosition(int startNum, int endNum, String position) {
		return iScheduleRepository.getScheduleListByPosition(startNum, endNum, position);
	}
	

}
