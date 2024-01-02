package com.example.vms.employee.model;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.manager.model.Employee;


//사용자 정보를 로드하는 역할 (사용자의 로그인 요청이 있을 때 호출됨)
@Component
public class EmployeeUserDetailsService implements UserDetailsService {

	@Autowired
	private IEmployeeService employeeService;

	// UserDetailsService 인터페이스의 메서드 구현
	@Override
	public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
	    try {
	        // 데이터베이스에서 사용자 정보 조회
	        Employee employeeinfo = employeeService.selectEmployee(empId);
	        System.out.println("sdffffffffffffffffffffffffffffffffssss");

	        // 추가 디버깅
	        System.out.println("Loaded Employee Info: " + employeeinfo);

	        // 조회한 사용자 정보가 없으면 예외 처리
	        if (employeeinfo == null) {
	            throw new UsernameNotFoundException("[" + empId + "] 사용자가 없어요");
	        }

	     // 사용자의 권한 문자열 배열 생성 (데이터베이스에서 조회)
          Set<String> roles = employeeService.getRolesByEmpId(empId);

          // 권한 리스트 생성
          List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles.toArray(new String[0]));
	        
          System.out.println("Loaded Employee Info: " + employeeinfo);
          System.out.println("Roles for Employee: " + roles);

          // EmployeeUserDetails 객체를 생성하여 반환
	        return new EmployeeUserDetails(employeeinfo.getEmpId(), employeeinfo.getPassword(), authorities,
	                employeeinfo.getDeptId());
	    } catch (Exception e) {
	        // 예외 로깅
	        System.out.println("Exception in loadUserByUsername: " + e.getMessage());
	        throw e; // 예외를 다시 던지기
	    }
	}

}
