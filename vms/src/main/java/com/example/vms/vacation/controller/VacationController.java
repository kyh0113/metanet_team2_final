package com.example.vms.vacation.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.model.VacationType;
import com.example.vms.vacation.service.VacationService;
import com.example.vms.vacation.service.VacationTypeService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
	
    @GetMapping("/request")
    public String requestVacation(Model model) {
    	List<VacationType> vacationTypes = vacationTypeService.getAllVacationType();
    	model.addAttribute("vacationTypes", vacationTypes);
        return "vacation/request";
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
    
  //휴가 신청서 목록 조회
  	@GetMapping("/request/list")
  	public String getRequestList(HttpServletRequest request, 
  			@RequestParam(name="curpage", defaultValue = "1") String curpage, 
  			Model model) {

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
          	//팀장인지 확인
          	Authentication auths = tokenProvider.getAuthentication(token);
          	Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
          	for (GrantedAuthority authority : authorities) {
          	    //String authorityName = authority.getAuthority();
          	    if(authority.getAuthority().equals("팀장")) {
          	    	return "vacation/requestlist_supervisor";
          	    }
          	}
          	return "vacation/requestlist_employee";
          
          } else {
          	return "redirect:/employee/login";
          }
  	}

    @ResponseBody
	@GetMapping("/request/getrow")
	public int getListRowNum(HttpServletRequest request, 
			@RequestParam(name="state", defaultValue = "") String state) {
		
    	int rowNum = 0;
		boolean isLeader = false;
		
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        
        //팀장인지 확인
      	Authentication auths = tokenProvider.getAuthentication(token);
      	Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
      	for (GrantedAuthority authority : authorities) {
      	    //String authorityName = authority.getAuthority();
      	    if(authority.getAuthority().equals("팀장")) {
      	    	isLeader = true;
      	    }
      	}
      	
        // 토큰에서 empId 추출
        String empId = tokenProvider.getEmpId(token);
        
		if(state.equals("")) {
			state = null;
		}
		
		if(isLeader) {
			rowNum = vacationService.getCountDeptRequestList(empId, state);
		} else {
			rowNum = vacationService.getCountRequestList(empId, state);
		}
		
    	return rowNum;
 
	}
	
	//팀원 휴가 신청서 목록 조회(비동기)
	@ResponseBody
	@GetMapping("/request/getlist")
	public List<VacationEmployee> getList(HttpServletRequest request, 
			@RequestParam(name="state", defaultValue = "") String state, 
			@RequestParam(name="curpage", defaultValue = "1") String curpage) {
		
		List<VacationEmployee> requestList = null;
		boolean isLeader = false;
		
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        
        //팀장인지 확인
      	Authentication auths = tokenProvider.getAuthentication(token);
      	Collection<? extends GrantedAuthority> authorities = auths.getAuthorities();
      	for (GrantedAuthority authority : authorities) {
      	    //String authorityName = authority.getAuthority();
      	    if(authority.getAuthority().equals("팀장")) {
      	    	isLeader = true;
      	    }
      	}
      	
        // 토큰에서 empId 추출
        String empId = tokenProvider.getEmpId(token);
        
		if(state.equals("")) {
			state = null;
		}
		
		if(isLeader) {
			requestList =  vacationService.getDeptRequestList(empId, state, curpage);
		} else {
			requestList =  vacationService.getRequestList(empId, state, curpage);
		}
		
		System.out.println(requestList);
		return requestList;
	}
    
	
	//휴가 신청서 상세 조회(팀장)
	@GetMapping("/request/list/team/{regId}")
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
		List<UploadFile> files = vacationService.getFileList(regIdNumber);
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
