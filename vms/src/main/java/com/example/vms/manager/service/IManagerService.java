package com.example.vms.manager.service;

import java.time.LocalDate;

import com.example.vms.manager.model.Employee;

public interface IManagerService {
	void create(Employee employee);
	Employee selectEmployee(String empId);
	String generateEmployeeId(int deptId, LocalDate hireDate);
	String encodePassword(String password);
}
