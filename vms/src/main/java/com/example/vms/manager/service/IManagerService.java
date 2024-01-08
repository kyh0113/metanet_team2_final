package com.example.vms.manager.service;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Param;

import com.example.vms.manager.model.Department;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;

public interface IManagerService {
	void create(Employee employee);
	Employee selectEmployee(String empId);
	String generateEmployeeId(int deptId, LocalDate hireDate);
	String encodePassword(String password);
	
    EmployeeResponseDTO[] searchEmployees(int start, int end);
    int numberOfEmployees();
	EmployeeResponseDTO searchEmployeeByEmpId(String empId);
	void updateEmployee(EmployeeUpdateRequestDTO employee);
	Department[] searchDepartments();
	
	int searchEmployeeRemains();
}
