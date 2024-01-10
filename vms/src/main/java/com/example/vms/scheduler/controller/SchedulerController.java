package com.example.vms.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.scheduler.model.SchedulerResult;
import com.example.vms.scheduler.service.ISchedulerService;

@Controller
@RequestMapping("/scheduler")
public class SchedulerController {

	@Autowired
	ISchedulerService schedulerService;
	
	@GetMapping("/list")
	@ResponseBody
	public SchedulerResult[] searchSchedulers(
		@RequestParam(name = "start", defaultValue = "0") int start,
		@RequestParam(name = "end", defaultValue = "10") int end, 
		@RequestParam(name = "success", defaultValue = "3", required = false) int success,
		@RequestParam(name = "conetent", required = false) String content 
	) {
		SchedulerResult[] schedulers = null;
		schedulers = schedulerService.searchSchedulers(start, end, content, 1);
		return schedulers;
	}
	
	// 스케줄러 목록 페이지 
	@GetMapping("/list/view")
	public String searchSchedulerPage() {
		return "/scheduler/list";
	}
	
}
