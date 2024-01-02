package com.example.vms.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.IManagerService;

@RestController
@RequestMapping("/manager")
public class ManagerController {
	
	@Autowired
	IManagerService managerService;
	
	@PostMapping("/create")
	public String create(@RequestBody List<Employee> employees) {
		for(Employee employee : employees) {
			managerService.create(employee);
		}
		return "직원이 성공적으로 생성되었습니다!";
	}
	
	@GetMapping("/{empId}")
	public Employee selectEmployeeInfo(@PathVariable String empId) {
		Employee employee = managerService.selectEmployee(empId);
		return employee;
	}
}
