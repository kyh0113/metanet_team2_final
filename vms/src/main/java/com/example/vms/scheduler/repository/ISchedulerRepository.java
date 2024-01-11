package com.example.vms.scheduler.repository;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.scheduler.model.SchedulerResult;

@Repository
@Mapper
public interface ISchedulerRepository {

	SchedulerResult[] searchSchedulers(
//		@Param("startDate") Date startDate,
//		@Param("endDate") Date endDate,
		@Param("start") int start,
		@Param("end") int end,
		@Param("content") String content,
		@Param("success") int success
	);
}
