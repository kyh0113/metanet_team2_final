package com.example.vms.employee.repository;

import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.employee.model.EmployeeVacationCountPerMonthResponseDTO;
import com.example.vms.employee.model.VacationApprovalWaiting;
import com.example.vms.manager.model.Employee;


@Repository
@Mapper
public interface IEmployeeRepository {
	Employee selectEmployee(String empId);
	Set<String> getRolesByEmpId(String empId);
	
	String selectEmpIdByNameEmail(Employee employee);
	Employee selectEmployeeInfoByIdNameEmail(Employee employee);
	int updatePassword(Employee employee);
	String selectDeptNameByEmpId(String empId);
	
	VacationApprovalWaiting[] searchVacationApprovalWaiting(
		@Param("empId") String empId, 
		@Param("state") String state, 
		@Param("position") String position
	);
	
	EmployeeVacationCountPerMonthResponseDTO[] searchEmployeeVacationCountPerMonth(
		@Param("empId") String empId
	);
	
	int numberOfVacationUsagesSearchByYear(
		@Param("year") String year
	);
}