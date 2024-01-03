package com.example.vms.schedule.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.service.IScheduleService;



@Controller
public class ScheduleController {
	@Autowired
	IScheduleService iScheduleService;

	@GetMapping("/cal")
	public String calendar(Model model) {
		List<Schedule> schedules = iScheduleService.getSchedulebydeptId(1);
		
		for(Schedule schedule : schedules) {
			System.out.println(schedule);
		}
		
		model.addAttribute("schedules",schedules);
		
		return "calendar/calendar";
	}
}
