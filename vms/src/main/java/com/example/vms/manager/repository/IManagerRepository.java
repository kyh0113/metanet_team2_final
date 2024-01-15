package com.example.vms.manager.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.example.vms.manager.model.Department;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;

@Repository
@Mapper
public interface IManagerRepository {
	void create(Employee employee);
	
	Employee selectEmployee(String empId);
	List<Employee> getAllEmployees();
	
	List<Employee> findEmployeesWithAtLeastOneVacation();
	
	// 부서 중 가장 큰 ID값의 뒤의 4자리 문자열을 숫자로 바꿔 가져오기
    @Select("SELECT MAX(TO_NUMBER(SUBSTR(EMP_ID, 4))) AS maxId FROM employees WHERE DEPT_ID = #{deptId}")
    Integer maxEmployeeId(@Param("deptId") int deptId);
    
    // 부서 이름 가져오기
    @Select("SELECT NAME FROM DEPARTMENTS WHERE DEPT_ID = #{deptId}")
    String departmentName(@Param("deptId") int deptId);
	
    EmployeeResponseDTO[] searchEmployees(@Param("start") int start, @Param("end") int end);
    EmployeeResponseDTO[] searchEmployeesWithEmpId(@Param("start") int start, @Param("end") int end, @Param("empId") String empId);
    int numberOfEmployees();
	EmployeeResponseDTO searchEmployeeByEmpId(@Param("empId") String empId);
	void updateEmployee(EmployeeUpdateRequestDTO employee);
	void updateRemains(String empId, int remains);
	Department[] searchDepartments();
	
	void deleteEmployeeRoles(@Param("empId") String empId);
	void insertEmployeeRole(@Param("role") String role, @Param("empId") String empId);
}
