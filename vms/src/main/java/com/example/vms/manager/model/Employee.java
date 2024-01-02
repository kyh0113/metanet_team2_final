package com.example.vms.manager.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Employee {
	private String empId; // 사원번호(아이디)
	private String name; // 사원의 이름
	private String password; // 비밀번호
	private String email; // 이메일
	private String position; // 직위 (대리, 차장, 부장, 이사 이런거) 
	private String gender; 
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birth;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate hireDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate retireDate;
	private String phone;
	private String status;
	private Integer remains;
	private Integer deptId;
}
