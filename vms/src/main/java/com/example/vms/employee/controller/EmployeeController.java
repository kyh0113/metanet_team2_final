package com.example.vms.employee.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.model.Mail;
import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.Response;
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
	

	@PostMapping("/login")
	public String login(@RequestParam String empId, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
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
	        System.out.println("토큰 출력: " + token);
	        
	        // 토큰을 클라이언트로 전송
	        // 헤더에서 토큰을 읽어와서 sessionStorage에 저장하기 위함 
	        //response.setHeader("X-AUTH-TOKEN", token); 
	        
	        Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
	        cookie.setMaxAge(-1);
	        cookie.setPath("/");
	        response.addCookie(cookie);
	        				
	        return "employee/home";
	    }
	}


	@GetMapping(value = "/test_jwt", produces = "text/plain")
	@ResponseBody
	public String testJwt(HttpServletRequest request) {
	    try {
	        String token = tokenProvider.resolveToken(request);
	        System.out.println("랄랄랄랄랄");
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
	
	// 아이디 찾기 기능
	@GetMapping("/find-employeeid")
    public String findEmployeeId(){
        return "employee/findEmpId";
    }
	
	@ResponseBody
	@PostMapping("/find-employeeid")
    public Result findEmployeeId(Employee employee){
		String msg = employeeService.findEmpId(employee);
		Result rst = new Result();
        rst.setResultMessage(msg);
        return rst;
    }
	// 비밀번호 찾기 기능
	@GetMapping("/find-password")
    public String findPassword(){
        return "employee/findPassword";
    }
	 
	@ResponseBody
	@PostMapping("/find-password")
	public Mail findPassword(Employee employee, HttpSession session) {
        Map<String, String> findPwdResult = employeeService.findPassword(employee);
        Mail mail = new Mail();
        mail.setSubject(findPwdResult.get("message"));
        mail.setContent(findPwdResult.get("authCode"));
        //session저장 -> 추후 변경해야 함
        session.setAttribute("empId", employee.getEmpId());
        session.setAttribute("name", employee.getName());
        session.setAttribute("email", employee.getEmail());
        return mail;
	    
	}
	
	// 비밀번호 변경 기능
	@GetMapping("/change-password")
    public String changePassword(){
        return "employee/changePassword";
    }
	 
	@ResponseBody
	@PostMapping("/change-password")
	public Result changePassword(String password, HttpSession session) {
		//비밀번호 변경
		Employee employee = new Employee();
		employee.setEmpId((String)session.getAttribute("empId"));
		employee.setName((String)session.getAttribute("name"));
		employee.setEmail((String)session.getAttribute("email"));
		// password 암호화
		PasswordEncoder pwEncoder = 
				PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String encodedPw = pwEncoder.encode(password);
		employee.setPassword(encodedPw);
		
		String result = employeeService.changePassword(employee);
        Result rst = new Result();
        rst.setResultMessage(result);
        return rst;
	}
}
