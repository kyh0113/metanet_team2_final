package com.example.vms.employee.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.manager.model.Employee;

@Service
public class EmployeeService implements IEmployeeService{
	
	@Autowired
	IEmployeeRepository employeeDao;

	@Override
	public Employee selectEmployee(String empId) {
		return employeeDao.selectEmployee(empId);
	}

	@Override
	public Set<String> getRolesByEmpId(String empId) {
		return employeeDao.getRolesByEmpId(empId);
	}

}
