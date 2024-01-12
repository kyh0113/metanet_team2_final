package com.example.vms.scheduler.service;

import com.example.vms.scheduler.model.Scheduler;
import com.example.vms.scheduler.model.SchedulerResult;

public interface ISchedulerService {
	void certificateScheduler();
	void saveScheduler(Scheduler scheduler);
	public int maxSchedulerId();
	
	SchedulerResult[] searchSchedulers(
//		Date startDate,
//		Date endDate,
		int start,
		int end,
		String content,
		int success
	);
}
