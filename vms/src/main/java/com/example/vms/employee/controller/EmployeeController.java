package com.example.vms.employee.controller;

import java.lang.ProcessHandle.Info;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.certificate.service.ICertificateService;
import com.example.vms.employee.model.EmployeeVacationCountPerMonth;
import com.example.vms.employee.model.Mail;
import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.service.IManagerService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.ws.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/employee")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmployeeController {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private IManagerService managerService;
	
	@Autowired
	private ICertificateService certificateService;

	@GetMapping("/login")
	public String showLoginPage() {
		return "employee/login";
	}

	@GetMapping("/home")
	public String home() {
		return "/employee/home";
	}

	
	// 로그아웃 기능 ( 보완 필요 ) 
	@GetMapping("/logout")
	public String logout(
		HttpServletRequest request, HttpServletResponse response
	) {

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				cookie.setMaxAge(0); 
		        cookie.setPath("/"); 
			}
		}
		
        //Cookie cookie = new Cookie("X-AUTH-TOKEN", null);
        
	    //response.addCookie(cookie);
		return "redirect:/employee/login";
	}

	@PostMapping("/login")

	public ResponseEntity<String> login(@RequestParam String empId, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
	    log.info("EMP_ID: {}", empId);
	    System.out.println("로그인로그이");

	    Employee employee = employeeService.selectEmployee(empId);
	    if (employee == null) {
	        System.out.println("로그인 실패1");
	        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
	        System.out.println("서버 응답: " + responseEntity.getBody());
	        return responseEntity;
	    }

	    if (!passwordEncoder.matches(password, employee.getPassword())) {
	        System.out.println("로그인 실패2");
	        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
	        System.out.println("서버 응답: " + responseEntity.getBody());
	        return responseEntity;
	    } else {
	        log.info("로그인 성공");

	        String token = tokenProvider.generateToken(employee);
	        System.out.println("토큰 출력: " + token);

	        Cookie cookie = new Cookie("X-AUTH-TOKEN", token);
	        cookie.setMaxAge(-1);
	        cookie.setPath("/");
	        response.addCookie(cookie);

			ResponseEntity<String> responseEntity = null;
	        
	        Set<String> roles = employeeService.getRolesByEmpId(employee.getEmpId());
	        if(roles.contains("MANAGER")) {
				responseEntity = ResponseEntity.ok("redirect:/manager/employee/list");
	        }
			else {
				responseEntity = ResponseEntity.ok("redirect:/employee/main");
			}
	        				
	        System.out.println("서버 응답: " + responseEntity.getBody());
	        return responseEntity;
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
	
	// 메인 페이지 
	@GetMapping("/main")
	public String mainPage(
		HttpServletRequest request,
		Model model 
	) {
		
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        //System.out.println(cookies.toString());
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }

        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) {
        	System.out.println("유효성 검사 들어옴");
            // 토큰에서 empId 추출
            String empId = tokenProvider.getEmpId(token);
    		EmployeeResponseDTO employee = managerService.searchEmployeeByEmpId(empId);
    		model.addAttribute("employee", employee);
    		int vacationUsageThisYear = employeeService.numberOfVacationUsagesSearchByYear(empId, "2024");
    		model.addAttribute("numberOfYearUsage", vacationUsageThisYear+"/"+(vacationUsageThisYear+employee.getRemains()));
    		int numberOfVacationApproval = employeeService.searchVacationApprovalWaiting(empId).length;
    		model.addAttribute("numberOfVacationApproval", numberOfVacationApproval);
    		int numberOfMyCertificates = certificateService.searchCertificatesByEmpId(empId).length;
    		model.addAttribute("numberOfMyCertificates", numberOfMyCertificates);
    		List<EmployeeVacationCountPerMonth> employeeVacationInfos = employeeService.vacationCountPerMonth(empId);
    		model.addAttribute("employeeVacationInfos", employeeVacationInfos);
    		
    		return "/employee/main";

        } else {
            // 토큰이 유효하지 않으면 로그인 페이지로 리다이렉트 또는 에러 처리
            return "redirect:/employee/login"; // 또는 다른 처리 방식을 선택하세요.
        }
		
	}
}
