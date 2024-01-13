package com.example.vms.leader.controller;

import java.time.Period;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.schedule.service.ScheduleService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.service.VacationService;
import com.example.vms.vacation.service.VacationTypeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/leader")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LeaderController {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private VacationService vacationService;

	@Autowired
	private ManagerService managerService;

	@Autowired
	private EmployeeService employeeService;

	
	@GetMapping("/request/list")
	public String getRequest(HttpServletRequest request,
			@RequestParam(name = "curpage", defaultValue = "1") String curpage,
			@RequestParam(name = "return", defaultValue = "false") String returnString, Model model) {

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
			
			// 팀장인지 확인
			Authentication auths = tokenProvider.getAuthentication(token);
			Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
			for (GrantedAuthority authority : authorities) {
				// String authorityName = authority.getAuthority();
				if (authority.getAuthority().equals("ROLE_LEADER")) {
					model.addAttribute("return", returnString);
					return "vacation/requestlist_supervisor";
				}
			}
			return "redirect:/employee/login";

		} else {
			return "redirect:/employee/login";
		}
	}
	
	@ResponseBody
	@GetMapping("/request/getrow")
	public int getRequestRowNum(HttpServletRequest request,
			@RequestParam(name = "state", defaultValue = "") String state) {

		int rowNum = 0;
		boolean isLeader = false;

		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}

		// 팀장인지 확인
		Authentication auths = tokenProvider.getAuthentication(token);
		Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			// String authorityName = authority.getAuthority();
			if (authority.getAuthority().equals("ROLE_LEADER")) {
				isLeader = true;
			}
		}

		// 토큰에서 empId 추출
		String empId = tokenProvider.getEmpId(token);

		if (state.equals("")) {
			state = null;
		}

		if (isLeader) {
			rowNum = vacationService.getCountDeptRequestList(empId, state);
		}

		return rowNum;

	}

	@ResponseBody
	@GetMapping("/request/getlist")

	public List<VacationEmployee> getRequestList(HttpServletRequest request,
			@RequestParam(name = "state", defaultValue = "") String state,
			@RequestParam(name = "curpage", defaultValue = "1") String curpage) {

		List<VacationEmployee> requestList = null;
		boolean isLeader = false;

		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}

		// 팀장인지 확인
		Authentication auths = tokenProvider.getAuthentication(token);
		Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			// String authorityName = authority.getAuthority();
			if (authority.getAuthority().equals("ROLE_LEADER")) {
				isLeader = true;
			}
		}

		// 토큰에서 empId 추출
		String empId = tokenProvider.getEmpId(token);

		if (state.equals("")) {
			state = null;
		}

		if (isLeader) {
			requestList = vacationService.getDeptRequestList(empId, state, curpage);
		} 

		return requestList;
	}

	// 휴가 신청서 상세 조회(팀장)
	@GetMapping("/request/list/team/{regId}")
	public String getRequestDetail(@PathVariable String regId, Model model) {
		// role이 팀장인지 확인 필요
		int regIdNumber = Integer.parseInt(regId);
		Vacation vacation = vacationService.getRequestDetail(regIdNumber);
		// 사원 정보 검색
		Employee employee = managerService.selectEmployee(vacation.getEmpId());
		// 부서 이름 검색
		String deptName = vacationService.getDeptNameByEmpId(vacation.getEmpId());
		// 휴가 유형 검색
		String typeName = vacationService.getVacationTypeName(vacation.getTypeId());
		// 휴가일수 계산
		Period period = Period.between(vacation.getStartDate(), vacation.getEndDate());
		// 파일 리스트 가져오기
		List<UploadFile> files = vacationService.getFileList(regIdNumber);
		System.out.println(files);
		System.out.println(vacation);
		model.addAttribute("vacation", vacation);
		model.addAttribute("employee", employee);
		model.addAttribute("deptName", deptName);
		model.addAttribute("typeName", typeName);
		model.addAttribute("vacationPeriod", period.getDays() + 1);
		model.addAttribute("files", files);
		return "vacation/requestdetail";
	}

	// 휴가 신청서 결재(팀장)
	@ResponseBody
	@PostMapping("/approval")
	public Result approvalRequest(HttpServletRequest request, @RequestBody Vacation vacation, Model model) {
		boolean isLeader = false;
		
		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}

		// 팀장인지 확인
		Authentication auths = tokenProvider.getAuthentication(token);
		Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
		for (GrantedAuthority authority : authorities) {
			// String authorityName = authority.getAuthority();
			if (authority.getAuthority().equals("ROLE_LEADER")) {
				isLeader = true;
			}
		}
		Result result = new Result();
		
		if(isLeader) {
			String resultMsg = vacationService.approvalRequest(vacation);
			model.addAttribute("resultmessage", resultMsg);
			String urlString = "redirect:/vacation/request/" + vacation.getRegId();
			result.setResultMessage(resultMsg);
		}
		
		return result;
	}
}
