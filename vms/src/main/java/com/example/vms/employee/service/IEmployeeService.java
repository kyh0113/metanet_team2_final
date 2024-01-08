package com.example.vms.employee.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.example.vms.employee.model.EmployeeVacationCountPerMonth;
import com.example.vms.employee.model.VacationApprovalWaiting;
import com.example.vms.manager.model.Employee;

public interface IEmployeeService {
	Employee selectEmployee(String empId);
	Set<String> getRolesByEmpId(String empId);
	
	String findEmpId(Employee employee);
	String sendMail(String content, String email, String mailSubject, String mailMessage);
	Map<String, String> findPassword(Employee employee);
	String changePassword(Employee employee);
	
	VacationApprovalWaiting[] searchVacationApprovalWaiting(
		String empId
	);
	
	List<EmployeeVacationCountPerMonth> vacationCountPerMonth(String empId);
}