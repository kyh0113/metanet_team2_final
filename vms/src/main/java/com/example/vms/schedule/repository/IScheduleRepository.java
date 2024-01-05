package com.example.vms.schedule.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.schedule.model.Schedule;

@Repository
@Mapper
public interface IScheduleRepository {
	List<Schedule> getSchedulebydeptId(@Param("dept_id") int dept_id);
}