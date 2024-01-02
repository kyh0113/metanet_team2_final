package com.example.vms.employee.service;

import java.util.Map;
import java.util.Set;

import com.example.vms.manager.model.Employee;

public interface IEmployeeService {
	Employee selectEmployee(String empId);
	Set<String> getRolesByEmpId(String empId);
	
	String findEmpId(Employee employee);
	String sendMail(String content, String email, String mailSubject, String mailMessage);
	Map<String, String> findPassword(Employee employee);
}