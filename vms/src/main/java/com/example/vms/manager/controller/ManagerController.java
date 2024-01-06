package com.example.vms.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;
import com.example.vms.manager.repository.IManagerRepository;
import com.example.vms.manager.service.IManagerService;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	@Autowired
	IManagerService managerService;
	
	@Autowired
	IManagerRepository managerRepository;
	
	@PostMapping("/create")
	@ResponseBody
	public String create(@RequestBody List<Employee> employees) {
		for(Employee employee : employees) {
			managerService.create(employee);
		}
		return "직원이 성공적으로 생성되었습니다!";
	}
	
	@GetMapping("/{empId}")
	@ResponseBody
	public Employee selectEmployeeInfo(@PathVariable String empId) {
		Employee employee = managerService.selectEmployee(empId);
		return employee;
	}
	
	@GetMapping("/employees")
	@ResponseBody
	public EmployeeResponseDTO[] searchEmployees(
		@RequestParam(name = "start", defaultValue = "0") int start,
		@RequestParam(name = "end", defaultValue = "10") int end 
	) {
		EmployeeResponseDTO[] employees = null;
		if (start <= end) {
			employees = managerService.searchEmployees(start, end);
		} 
		return employees;
	}
	
	@GetMapping("/employee/{empId}")
	@ResponseBody 
	public EmployeeResponseDTO searchEmployeeByEmpId(
		@PathVariable String empId
	) {
		return managerService.searchEmployeeByEmpId(empId);
	}
	
	@PostMapping("/employee/update")
	@ResponseBody
	public Employee updateEmployee(
		@RequestBody EmployeeUpdateRequestDTO employeeRequest
	) {
		managerRepository.updateEmployee(employeeRequest);
		return managerService.selectEmployee(employeeRequest.getEmpId());
	}
	
	// 직원 조회 페이지 
	@GetMapping("/employee/list")
	public String employeeListPage(
		Model model 
	) {
		model.addAttribute("numberOfEmployees", managerService.numberOfEmployees());
		return "/manager/list";
	}
}
