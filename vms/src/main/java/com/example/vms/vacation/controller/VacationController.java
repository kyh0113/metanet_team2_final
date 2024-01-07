package com.example.vms.vacation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationType;
import com.example.vms.vacation.service.VacationService;
import com.example.vms.vacation.service.VacationTypeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.vms.employee.model.Result;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.service.VacationService;

@Controller
@RequestMapping("/vacation")
public class VacationController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private VacationService vacationService;
    
    @Autowired
    private VacationTypeService vacationTypeService;

	@Autowired
	private ManagerService managerService;

    @GetMapping("/request")
    public String requestVacation(Model model) {
    	List<VacationType> vacationTypes = vacationTypeService.getAllVacationType();
    	model.addAttribute("vacationTypes", vacationTypes);
        return "vacation/request";
    }

    @PostMapping(path = "/request", consumes = "multipart/form-data; charset=UTF-8", produces = "application/json")
    public String requestVacation(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Vacation vacation,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
    	
    	// 종료 날짜 설정
        LocalDate endDate = LocalDate.parse(request.getParameter("endDate"));
        vacation.setEndDate(endDate);
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
        
        // 휴가 유형 확인을 위한 로그 추가
        System.out.println("Received typeId from form: " + vacation);

       
     
        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) {
        	System.out.println("유효성 검사 들어옴");
            // 토큰에서 empId 추출
            String empId = tokenProvider.getEmpId(token);

            // 휴가 정보 설정
            vacation.setEmpId(empId);
            

            // 휴가 등록
            vacationService.requestVacation(vacation, files);

            // 쿠키에 토큰을 설정 (클라이언트 사이드에서 사용)
            response.setHeader("Set-Cookie", "X-AUTH-TOKEN=" + token + "; Path=/; HttpOnly");

            return "redirect:/vacation/list";
        } else {
            // 토큰이 유효하지 않으면 로그인 페이지로 리다이렉트 또는 에러 처리
            return "redirect:/employee/login"; // 또는 다른 처리 방식을 선택하세요.
        }
    }

	//팀원 휴가 신청서 목록 조회(팀장)
	@GetMapping("/request/{empId}")
	public String getRequest(@PathVariable String empId, Model model) {
		//사원 아이디 확인(토큰으로) / 일단 pathvariable로 받아옴
		//role이 팀장인지 확인 필요
		List<VacationEmployee> requestList =  vacationService.getDeptRequestList(empId);
		System.out.println(requestList);
		model.addAttribute("requestList", requestList);
		return "vacation/requestlist";
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
		System.out.println(vacation);
		model.addAttribute("vacation", vacation);
		model.addAttribute("employee", employee);
		model.addAttribute("deptName", deptName);
		return "vacation/requestdetail";
	}
	
	//휴가 신청서 결재(팀장)
	@ResponseBody
	@PostMapping("/approval")
	public Result approvalRequest(@RequestBody Vacation vacation, Model model) {
		System.out.println(vacation);
		//role이 팀장인지 확인 필요
		String resultMsg = vacationService.approvalRequest(vacation);
		System.out.println(resultMsg);
		model.addAttribute("resultmessage", resultMsg);
		Result result = new Result();
		String urlString = "redirect:/vacation/request/"+vacation.getRegId();
		result.setResultMessage(resultMsg);
		return result;
	}
}
