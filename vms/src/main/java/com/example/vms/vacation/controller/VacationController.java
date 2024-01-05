package com.example.vms.vacation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.vms.employee.service.EmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.service.VacationService;

@Controller
@RequestMapping("/vacation")
public class VacationController {
	
	@Autowired
	VacationService vacationService;
	@Autowired
	ManagerService managerService;
	
	//팀원 휴가 신청서 목록 조회(팀장)
	@GetMapping("/request/{empId}")
	public String getRequest(@PathVariable String empId, Model model) {
		//사원 아이디 확인(토큰으로) / 일단 pathvariable로 받아옴
		//role이 팀장인지 확인 필요
		List<VacationEmployee> requestList =  vacationService.getDeptRequestList(empId);
		System.out.println(requestList);
		model.addAttribute("requestList", requestList);
		return "vacation/requestlist";
	}
	
	//휴가 신청서 상세 조회(팀장)
	@GetMapping("/requestd/{regId}")
	public String getRequestDetail(@PathVariable String regId, Model model) {
		//role이 팀장인지 확인 필요
		int regIdNumber = Integer.parseInt(regId);
		Vacation vacation = vacationService.getRequestDetail(regIdNumber);
		//사원 정보 검색
		Employee employee = managerService.selectEmployee(vacation.getEmpId());
		//부서 이름 검색
		String deptName = vacationService.getDeptNameByEmpId(vacation.getEmpId());
		System.out.println(vacation);
		model.addAttribute("vacation", vacation);
		model.addAttribute("employee", employee);
		model.addAttribute("deptName", deptName);
		return "vacation/requestdetail";
	}
	
	//휴가 신청서 결재(팀장)
	@PostMapping("/approval")
	public String approvalRequest(@RequestBody Vacation vacation, Model model) {
		//role이 팀장인지 확인 필요
		String result = vacationService.approvalRequest(vacation);
		System.out.println(result);
		model.addAttribute("resultmessage", result);
		return "redirect:/vacation/request/"+vacation.getRegId();
	}
}
