package com.example.vms.employee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/login")
	public String showLoginPage() {
		return "employee/login";
	}

	@GetMapping("/home")
	public String home() {
		return "employee/home";
	}

	// 로그아웃 기능
	// 비밀번호 변경 기능
	// 아이디 찾기 기능
	// 비밀번호 찾기 기능

	@PostMapping("/login")
	public String login(@RequestParam String empId, @RequestParam String password, Model model) {
		log.info("EMP_ID: {}", empId);

		Employee employee = employeeService.selectEmployee(empId);
		if (employee == null) {
			throw new IllegalArgumentException("사용자가 없습니다.");
		}

		if (!passwordEncoder.matches(password, employee.getPassword())) {
			throw new IllegalArgumentException("비밀번호 오류");
		} else {
			log.info("로그인 성공");

			String token = tokenProvider.generateToken(employee);
			System.out.println("토큰출력 : " + token);
			model.addAttribute("token", token);
		}

		return "redirect:/employee/home";
	}

	@GetMapping(value = "/test_jwt", produces = "text/plain")
	@ResponseBody
	public String testJwt(HttpServletRequest request) {
	    try {
	        String token = tokenProvider.resolveToken(request);
	        log.info("token {}", token); 
	        Authentication auth = tokenProvider.getAuthentication(token);
	        log.info("principal {}, name {}, authorities {}",
	                auth.getPrincipal(), auth.getName(), auth.getAuthorities());
	        log.info("isValid {}", tokenProvider.validateToken(token));
	        tokenProvider.getEmpId(token);
	        return "JWT test successful";
	    } catch (Exception e) {
	        log.error("Exception during JWT test", e);
	        return "JWT test failed: " + e.getMessage();
	    }
	}
}
