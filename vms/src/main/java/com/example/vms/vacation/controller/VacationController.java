package com.example.vms.vacation.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Period;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.model.Mail;
import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.service.VacationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/vacation")
public class VacationController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private VacationService vacationService;

	@Autowired
	private ManagerService managerService;
	
	@Autowired
	private EmployeeService employeeService;
	
    @GetMapping("/request")
    public String requestVacation() {
        return "vacation/request";
    }

    @PostMapping("/request")
    public String requestVacation(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Vacation vacation) {
        // 토큰 추출
//        String token = tokenProvider.resolveToken(request);
//        System.out.println("쿠키로 토큰 가져옴 "+token); // 쿠키로 토큰 가져옴
    	
    		
    	
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

            // 휴가 정보 설정
            vacation.setEmpId(empId);

            // 휴가 등록
            vacationService.requestVacation(vacation);

            // 성공적으로 등록되었으면 리스트 페이지로 리다이렉트

            // 쿠키에 토큰을 설정 (클라이언트 사이드에서 사용)
            response.setHeader("Set-Cookie", "X-AUTH-TOKEN=" + token + "; Path=/; HttpOnly");

            return "redirect:/vacation/list";
        } else {
            // 토큰이 유효하지 않으면 로그인 페이지로 리다이렉트 또는 에러 처리
            return "redirect:/employee/login"; // 또는 다른 처리 방식을 선택하세요.
        }
    }

	//팀원 휴가 신청서 목록 조회(팀장)
	@GetMapping("/request/list")
	public String getRequest(@RequestParam(name="curpage", defaultValue = "1") String curpage, Model model) {
		//사원 아이디 확인(토큰으로) / 일단 pathvariable로 받아옴
		String empId = "I760001";
		//role이 팀장인지 확인 필요
		
		String state = null;
		int rowNum = vacationService.getCountDeptRequestList(empId, state);
		int waitingRowNum = vacationService.getCountDeptRequestList(empId, "결재대기");
		System.out.println(rowNum);
		List<VacationEmployee> requestList =  vacationService.getDeptRequestList(empId, state, curpage);
		System.out.println(requestList);
		model.addAttribute("requestList", requestList);
		model.addAttribute("rowNum", rowNum);
		model.addAttribute("waitingRowNum", waitingRowNum);
		return "vacation/requestlist";
	}
	
	//팀원 휴가 신청서 목록 조회(비동기)
	@ResponseBody
	@GetMapping("/request/state")
	public List<VacationEmployee> getRequest(@RequestParam(name="state", defaultValue = "") String state, @RequestParam(name="curpage", defaultValue = "1") String curpage) {
		//사원 아이디 확인(토큰으로) / 일단 pathvariable로 받아옴
		//role이 팀장인지 확인 필요
		String empId = "I760001";
		List<VacationEmployee> requestList = null;
		if(state.equals("")) {
			state = null;
		}
		requestList =  vacationService.getDeptRequestList(empId, state, curpage);
		System.out.println(requestList);
		return requestList;
	}
	
	//휴가 신청서 상세 조회(팀장)
	@GetMapping("/requestd/{regId}")
	public String getRequestDetail(@PathVariable String regId, Model model) {
		//role이 팀장인지 확인 필요
		int regIdNumber = Integer.parseInt(regId);
		Vacation vacation = vacationService.getRequestDetail(regIdNumber);
		//사원 정보 검색
		Employee employee = managerService.selectEmployee(vacation.getEmpId());
		//부서 이름 검색
		String deptName = vacationService.getDeptNameByEmpId(vacation.getEmpId());
		//휴가 유형 검색
		String typeName = vacationService.getVacationTypeName(vacation.getTypeId());
		//휴가일수 계산
		Period period = Period.between(vacation.getStartDate(), vacation.getEndDate());
		//파일 리스트 가져오기
		List<String> files = vacationService.getFileList(regIdNumber);
		System.out.println(files);
		System.out.println(vacation);
		model.addAttribute("vacation", vacation);
		model.addAttribute("employee", employee);
		model.addAttribute("deptName", deptName);
		model.addAttribute("typeName", typeName);
		model.addAttribute("vacationPeriod", period.getDays()+1);
		model.addAttribute("files", files);
		return "vacation/requestdetail";
	}
	
	//휴가 신청서 결재(팀장)
	@ResponseBody
	@PostMapping("/approval")
	public Result approvalRequest(@RequestBody Vacation vacation, Model model) {
		//role이 팀장인지 확인 필요
		String resultMsg = vacationService.approvalRequest(vacation);
		model.addAttribute("resultmessage", resultMsg);
		Result result = new Result();
		String urlString = "redirect:/vacation/request/"+vacation.getRegId();
		result.setResultMessage(resultMsg);
		return result;
	}
	
	//메일 전송
	@ResponseBody
	@PostMapping("/send-mail")
	public Result sendMail(@RequestBody Mail mail) {
		
		String sendResult = employeeService.sendMail(mail.getContent(), mail.getReceiver(), mail.getSubject(), mail.getMessage());
		Result result = new Result();
		result.setResultMessage(sendResult);
		return result;
	}
	
	//파일 다운로드(비동기)
	@ResponseBody
	@PostMapping("/file-download/{fileId}") 
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
