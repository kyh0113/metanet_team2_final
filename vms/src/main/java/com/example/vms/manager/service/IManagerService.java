package com.example.vms.manager.service;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.vms.manager.model.Department;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;

public interface IManagerService {
	void create(Employee employee);
	Employee selectEmployee(String empId);
	List<Employee> getAllEmployees();
	String generateEmployeeId(int deptId, LocalDate hireDate);
	String encodePassword(String password);
	List<Employee> findEmployeesWithAtLeastOneVacation();
	
    EmployeeResponseDTO[] searchEmployees(int start, int end, String empId);
    int numberOfEmployees();
	EmployeeResponseDTO searchEmployeeByEmpId(String empId);
	void updateEmployee(EmployeeUpdateRequestDTO employee);
	void updateRemains(String empId, int remains);
	Department[] searchDepartments();
	
	int searchEmployeeRemains();
}
