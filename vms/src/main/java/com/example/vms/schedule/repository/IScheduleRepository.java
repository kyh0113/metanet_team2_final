package com.example.vms.schedule.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;

@Repository
@Mapper
public interface IScheduleRepository {
	List<Schedule> getSchedulebydeptId(@Param("dept_id") int dept_id);

	int getCountAllSchedule();
	List<ScheduleEmpDeptType> getAllScheduleList(@Param("startNum") int startNum, @Param("endNum") int endNum);
	int getCountScheduleByEmpName(String empName);
	List<ScheduleEmpDeptType> getScheduleListByEmpName(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("empName") String empName);
	int getCountScheduleByDeptName(String deptName);
	List<ScheduleEmpDeptType> getScheduleListByDeptName(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("deptName") String deptName);
	int getCountScheduleByPosition(String position);
	List<ScheduleEmpDeptType> getScheduleListByPosition(@Param("startNum") int startNum, @Param("endNum") int endNum, @Param("position") String position);
	
	void insertSchedule(Schedule schedule);
	int maxScheduleId();
}
