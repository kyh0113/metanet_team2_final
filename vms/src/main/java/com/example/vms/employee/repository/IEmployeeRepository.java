package com.example.vms.employee.repository;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.manager.model.Employee;


@Repository
@Mapper
public interface IEmployeeRepository {
	Employee selectEmployee(String empId);
	Set<String> getRolesByEmpId(String empId);
	
	String selectEmpIdByNameEmail(Employee employee);
	Employee selectEmployeeInfoByIdNameEmail(Employee employee);
}