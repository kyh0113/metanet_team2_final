package com.example.vms.schedule.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;

public interface IScheduleService {

	List<Schedule> getSchedulebydeptId(int dept_id);
	void insertSchedule(Schedule schedule);
	int maxScheduleId();
	
	int getCountAllSchedule();
	List<ScheduleEmpDeptType> getAllScheduleList(int startNum, int endNum);
	int getCountScheduleByEmpName(String empName);
	List<ScheduleEmpDeptType> getScheduleListByEmpName(int startNum, int endNum, String empName);
	int getCountScheduleByDeptName(String deptName);
	List<ScheduleEmpDeptType> getScheduleListByDeptName(int startNum, int endNum, String deptName);
	int getCountScheduleByPosition(String position);
	List<ScheduleEmpDeptType> getScheduleListByPosition(int startNum, int endNum, String position);
}
