package com.example.vms.schedule.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.service.IScheduleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class ScheduleController {

	private final IScheduleService iScheduleService;

	@GetMapping("/look")
	public String calendar(Model model) {	

		return "calendar/calendar";
	}

	@GetMapping("/calendar-admin")
	@ResponseBody
	public List<Map<String, Object>> monthPlan() {
		List<Schedule> schedules = iScheduleService.getSchedulebydeptId(2);

		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		HashMap<String, Object> hash = new HashMap<>();

		for (int i = 0; i < schedules.size(); i++) {
			hash.put("title", schedules.get(i).getTitle());
			hash.put("start", schedules.get(i).getStart_date());
			hash.put("end", schedules.get(i).getEnd_date());
			hash.put("allDay",true);
            // hash.put("time", listAll.get(i).getScheduleTime());
			
			int color = schedules.get(i).getType_id();
			System.out.println(hash.get("start"));
			switch(color) {
			case 1:
				hash.put("color","#0000FF");
				break;
			case 2:
				hash.put("color","#00FF00");
				break;
			case 3:
				hash.put("color","orange");
				break;
			default:
				hash.put("color","#000000");
			}
			
			jsonObj = new JSONObject(hash);
			jsonArr.add(jsonObj);
		}

		return jsonArr;
	}
}