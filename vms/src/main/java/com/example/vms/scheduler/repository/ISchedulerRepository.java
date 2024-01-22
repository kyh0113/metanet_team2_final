package com.example.vms.scheduler.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.scheduler.model.Scheduler;
import com.example.vms.scheduler.model.SchedulerResult;

@Repository
@Mapper
public interface ISchedulerRepository {
	
	void saveScheduler(Scheduler scheduler);
	public int maxSchedulerId();

	SchedulerResult[] searchSchedulers(
//		@Param("startDate") Date startDate,
//		@Param("endDate") Date endDate,
		@Param("start") int start,
		@Param("end") int end,
		@Param("content") String content,
		@Param("success") int success
	);
}
