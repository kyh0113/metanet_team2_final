package com.example.vms.manager.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;
import com.example.vms.manager.repository.IManagerRepository;
import com.example.vms.manager.service.IManagerService;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.vacation.service.VacationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	@Autowired
	IManagerService managerService;
	@Autowired
	JwtTokenProvider tokenProvider;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private VacationService vacationService;
	
	@PostMapping("/create")
	@ResponseBody
	public String create(@RequestBody List<Employee> employees) {
		for(Employee employee : employees) {
			managerService.create(employee);
		}
		return "직원이 성공적으로 생성되었습니다!";
	}
	
	@GetMapping("/{empId}")
	@ResponseBody
	public Employee selectEmployeeInfo(@PathVariable String empId) {
		Employee employee = managerService.selectEmployee(empId);
		return employee;
	}
	
	@GetMapping("/employees")
	@ResponseBody
	public EmployeeResponseDTO[] searchEmployees(
		@RequestParam(name = "start", defaultValue = "0") int start,
		@RequestParam(name = "end", defaultValue = "10") int end,
		@RequestParam(name = "empId", required = false) String empId
	) {
		EmployeeResponseDTO[] employees = null;
		if (start <= end) {
			employees = managerService.searchEmployees(start, end, empId);
		} 
		return employees;
	}
	
	@GetMapping("/employee/{empId}")
	@ResponseBody 
	public EmployeeResponseDTO searchEmployeeByEmpId(
		@PathVariable String empId
	) {
		return managerService.searchEmployeeByEmpId(empId);
	}
	
	@PostMapping("/employee/update")
	@ResponseBody
	public Employee updateEmployee(
		@RequestBody EmployeeUpdateRequestDTO employeeRequest
	) {
		managerService.updateEmployee(employeeRequest);
	    return managerService.selectEmployee(employeeRequest.getEmpId());
	}
	
	// 직원 조회 페이지 
	@GetMapping("/employee/list")
	public String employeeListPage(
		HttpServletRequest request,
		Model model 
	) {
		
		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}
		// 토큰 유효성 검사
		if (tokenProvider.validateToken(token)) {
			
			String empId = tokenProvider.getEmpId(token);
	        Employee employee = managerService.selectEmployee(empId);
	        model.addAttribute("employee", employee);
			model.addAttribute("numberOfEmployees", managerService.numberOfEmployees());
			model.addAttribute("auth", "MANAGER");
			return "/manager/list";
	        
		} else {
			return "redirect:/employee/login";
		}

	}
	
	// 직원 수정 페이지 
	@GetMapping("/employee/update/{empId}")
	public String updateEmployeePage(
		@PathVariable String empId,
		Model model 
	) {
		EmployeeResponseDTO employee = managerService.searchEmployeeByEmpId(empId);
		String[] autorities = {"팀원", "팀장", "관리자"};
		String[] positions = {"사원", "대리", "과장", "차장", "부장"};
		model.addAttribute("employee", employee);
		model.addAttribute("departments", managerService.searchDepartments());
		model.addAttribute("positions", positions);
		model.addAttribute("authorities", autorities);
		return "/manager/employeeupdate";
	}
	
	// 모든 사원 일정 조회(관리자)
	@GetMapping("/schedule/list")
	public String getSchedule(HttpServletRequest request, Model model) {

		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}
		// 토큰 유효성 검사
		if (tokenProvider.validateToken(token)) {
			
			String empId = tokenProvider.getEmpId(token);
	        Employee employee = employeeService.selectEmployee(empId);
	        model.addAttribute("employee", employee);
			model.addAttribute("auth", "MANAGER");
			return "manager/schedulelist";

		} else {
			return "redirect:/employee/login";
		}
	}

	@ResponseBody
	@GetMapping("/schedule/getrow")
	public int getScheduleRowNum(@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "option", required = false, defaultValue = "0") String option) {

		int rowNum = 0;

		// 쿠키 정보
//		        Cookie[] cookies = request.getCookies();
//		        String token = "";
//		        for(Cookie cookie : cookies) {
//		           if(cookie.getName().equals("X-AUTH-TOKEN")) {
//		              token = cookie.getValue();
//		           }
//		        }

		// 토큰에서 empId 추출
		// String empId = tokenProvider.getEmpId(token);

		rowNum = vacationService.getCountScheduleByOption(keyword, Integer.parseInt(option));

		return rowNum;
	}

	@ResponseBody
	@GetMapping("/schedule/getlist")
	public List<ScheduleEmpDeptType> getScheduleList(
			@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(name = "option", required = false, defaultValue = "0") String option,
			@RequestParam(name = "curPage", required = false, defaultValue = "1") String curPage) {

		List<ScheduleEmpDeptType> schedulList = vacationService.getScheduleListByOption(curPage, keyword,
				Integer.parseInt(option));

		System.out.println("schedule" + schedulList);
		return schedulList;
	}
	
	// 스케줄러 목록 페이지
	@GetMapping("/scheduler/list")
	public String searchSchedulerPage(
		HttpServletRequest request,
		Model model 
	) {
		
		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}
		// 토큰 유효성 검사
		if (tokenProvider.validateToken(token)) {
			
			String empId = tokenProvider.getEmpId(token);
	        Employee employee = managerService.selectEmployee(empId);
	        model.addAttribute("employee", employee);
	        model.addAttribute("auth", "MANAGER");
			return "/scheduler/list";		
		} else {
			return "redirect:/employee/login";
		}
		
	}
	
    // 증명서 진위 확인 화면 
    @GetMapping("/certificate/verificate")
    public String verificatePage(
    	HttpServletRequest request,
        Model model
    ) {
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) { 
            // 토큰에서 empId 추출
            String empId = tokenProvider.getEmpId(token);
    		EmployeeResponseDTO employee = managerService.searchEmployeeByEmpId(empId);
    		model.addAttribute("employee", employee);
    		model.addAttribute("auth", "MANAGER");
            return "certificate/certificateverificationpage";      	
        }
        
        return "redirect:/employee/login";

    }

}
