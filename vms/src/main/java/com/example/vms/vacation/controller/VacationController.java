package com.example.vms.vacation.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.employee.model.Mail;
import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;

import com.example.vms.schedule.service.ScheduleService;
import com.example.vms.vacation.model.FileDownload;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.model.VacationType;
import com.example.vms.vacation.service.VacationService;
import com.example.vms.vacation.service.VacationTypeService;

import jakarta.mail.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/vacation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VacationController {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private VacationService vacationService;

	@Autowired
	private VacationTypeService vacationTypeService;

	@Autowired
	private ManagerService managerService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ScheduleService scheduleService;

	@GetMapping("/request")
	public String requestVacation(
		HttpServletRequest request,
		Model model, HttpSession session
	) {
		
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
			// 토큰에서 empId 추출

			List<VacationType> vacationTypes = vacationTypeService.getAllVacationType();
			model.addAttribute("vacationTypes", vacationTypes);
			
			String csrfToken = UUID.randomUUID().toString();  // UUID를 생성하여 세션에 저장
			session.setAttribute("csrfToken", csrfToken);

			return "vacation/request";

		} else {
			return "redirect:/employee/login";
		}
	
	}

	@GetMapping("/getDaysForVacationType")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> getDaysForVacationType(@RequestParam int typeId) {
		System.out.println("하이헬로");
		int days = vacationTypeService.findDaysByTypeId(typeId);
		System.out.println(days);
		Map<String, Integer> response = new HashMap<>();
		response.put("days", days);
		System.out.println(response);
		return ResponseEntity.ok(response);
	}

	@PostMapping(path = "/request", consumes = "multipart/form-data; charset=UTF-8", produces = "application/json")
	public String requestVacation(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute Vacation vacation, @RequestParam(value = "files", required = false) MultipartFile[] files,
			Model model, HttpSession session, String csrfToken) {

		// 종료 날짜 설정
		LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
		vacation.setEndDate(endDate);
		// 토큰 추출
//        String token = tokenProvider.resolveToken(request);
//        System.out.println("쿠키로 토큰 가져옴 "+token); // 쿠키로 토큰 가져옴

		// 쿠키 정보
		Cookie[] cookies = request.getCookies();

		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}

		// 휴가 유형 확인을 위한 로그 추가
		System.out.println("Received typeId from form: " + vacation);

		// 토큰 유효성 검사
		if (tokenProvider.validateToken(token)) {
			
			if(csrfToken==null || "".equals(csrfToken)) {// 요청 파라미터의 UUID와 세션의 UUID를 비교하여 같을 경우에만 처리
				throw new RuntimeException("CSRF 토큰이 없습니다.");
			}else if(!csrfToken.equals(session.getAttribute("csrfToken"))) {
				throw new RuntimeException("잘 못된 접근이 감지되었습니다.");
			}
			// 토큰에서 empId 추출
			String empId = tokenProvider.getEmpId(token);

	        Employee employee = employeeService.selectEmployee(empId);
	        model.addAttribute("employee", employee);
			
			// 휴가 정보 설정
			vacation.setEmpId(empId);
			
			vacation.setVacationDays(calculateWorkingDaysBetween(vacation.getStartDate(), vacation.getEndDate()));

			// 휴가 등록
			vacationService.requestVacation(vacation, files);

			// 쿠키에 토큰을 설정 (클라이언트 사이드에서 사용)
			response.setHeader("Set-Cookie", "X-AUTH-TOKEN=" + token + "; Path=/; HttpOnly");

			return "redirect:/vacation/request/list";
		} else {
			// 토큰이 유효하지 않으면 로그인 페이지로 리다이렉트 또는 에러 처리
			return "redirect:/employee/login"; // 또는 다른 처리 방식을 선택하세요.
		}
	}

	// 휴가 신청서 목록 조회
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
			
			return "vacation/requestlist_employee";

		} else {
			return "redirect:/employee/login";
		}
	}

	@GetMapping("/request/detail/{regId}")
	public String getRequestDetailEmployee(@PathVariable String regId, Model model) {
		int regIdNumber = Integer.parseInt(regId);
		Vacation vacation = vacationService.getRequestDetail(regIdNumber);
		// 사원 정보 검색
		Employee employee = managerService.selectEmployee(vacation.getEmpId());
		// 부서 이름 검색
		String deptName = vacationService.getDeptNameByEmpId(vacation.getEmpId());
		// 휴가 유형 검색
		String typeName = vacationService.getVacationTypeName(vacation.getTypeId());
		// 휴가일수 계산
		int workingDays = calculateWorkingDaysBetween(vacation.getStartDate(), vacation.getEndDate());
		
		// 파일 리스트 가져오기
		List<UploadFile> files = vacationService.getFileList(regIdNumber);
		System.out.println(files);
		
		System.out.println(vacation);
		model.addAttribute("vacation", vacation);
		model.addAttribute("employee", employee);
		model.addAttribute("deptName", deptName);
		model.addAttribute("typeName", typeName);
		model.addAttribute("vacationPeriod", workingDays);
		model.addAttribute("files", files);
		return "vacation/requestdetail_employee";
	}
	
	
	public static int calculateWorkingDaysBetween(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
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

		// 토큰에서 empId 추출
		String empId = tokenProvider.getEmpId(token);

		if (state.equals("")) {
			state = null;
		}

		rowNum = vacationService.getCountRequestList(empId, state);

		return rowNum;

	}

	@ResponseBody
	@GetMapping("/request/getlist")

	public List<VacationEmployee> getRequestList(HttpServletRequest request,
			@RequestParam(name = "state", defaultValue = "") String state,
			@RequestParam(name = "curpage", defaultValue = "1") String curpage) {

		List<VacationEmployee> requestList = null;
		
		// 쿠키 정보
		Cookie[] cookies = request.getCookies();
		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}

		// 토큰에서 empId 추출
		String empId = tokenProvider.getEmpId(token);

		if (state.equals("")) {
			state = null;
		}

		requestList = vacationService.getRequestList(empId, state, curpage);

		System.out.println(requestList);
		return requestList;
	}

	@PostMapping("/delete/{regId}")
	@ResponseBody
	public ResponseEntity<String> deleteRequest(@PathVariable int regId) {
		try {
			System.out.println("랄랄: " + regId);
			vacationService.deleteVacation(regId);
			return ResponseEntity.ok().body("{\"resultMessage\": \"삭제가 성공적으로 이루어졌습니다.\"}");
		} catch (DataAccessException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"resultMessage\": \"데이터베이스 오류가 발생했습니다.\"}");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"resultMessage\": \"삭제 중 오류가 발생했습니다.\"}");
		}
	}

	// 메일 전송
	@ResponseBody
	@PostMapping("/send-mail")
	public Result sendMail(@RequestBody Mail mail) {

		String sendResult = employeeService.sendMail(mail.getContent(), mail.getReceiver(), mail.getSubject(),
				mail.getMessage());
		Result result = new Result();
		result.setResultMessage(sendResult);
		return result;
	}

	// 파일 다운로드
	@GetMapping("/file-download/{fileId}")
	public ResponseEntity<byte[]> getFile(@PathVariable int fileId) {
		UploadFile file = vacationService.getFile(fileId);
		System.out.println(file.toString());
		final HttpHeaders headers = new HttpHeaders();
		String[] mtypes = file.getContentType().split("/");
		headers.setContentType(new MediaType(mtypes[0], mtypes[1]));
		headers.setContentLength(file.getFileSize());
		try {
			String encodedFileName = URLEncoder.encode(file.getName(), "UTF-8");
			headers.setContentDispositionFormData("attachment", encodedFileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return new ResponseEntity<byte[]>(file.getFileData(), headers, HttpStatus.OK);
	}

}