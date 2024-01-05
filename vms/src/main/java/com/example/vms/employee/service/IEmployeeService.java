package com.example.vms.employee.service;

import java.util.Set;

import com.example.vms.manager.model.Employee;

public interface IEmployeeService {
	Employee selectEmployee(String empId);
	Set<String> getRolesByEmpId(String empId);
}