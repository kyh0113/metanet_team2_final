package com.example.vms.employee.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

//사원의 인증 및 권한 정보를 담고 있음
public class EmployeeUserDetails extends User{
	
	private static final long serialVersionUID = 2039986090449208134L;
	private int deptId;
	
	// EmployeeUserDetails 객체 생성자
	public EmployeeUserDetails(String empId, String password, Collection<? extends GrantedAuthority> authorities,
			int deptId) {
		// User 클래스의 생성자를 호출하여 사용자 아이디, 비밀번호, 권한 정보 초기화
		super(empId, password, authorities);
		this.deptId=deptId;
	}
	
	public int getDeptId() {
		return this.deptId;
	}

}