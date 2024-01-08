package com.example.vms.employee.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.vms.employee.model.EmployeeVacationCountPerMonth;
import com.example.vms.employee.model.EmployeeVacationCountPerMonthResponseDTO;
import com.example.vms.employee.model.VacationApprovalWaiting;
import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.manager.model.Employee;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService{
	
	@Autowired
	IEmployeeRepository employeeDao;
	
	private final JavaMailSender javaMailSender;
    private static final String senderEmail= "hwjin1999@gmail.com";

	@Override
	public Employee selectEmployee(String empId) {
		return employeeDao.selectEmployee(empId);
	}

	@Override
	public Set<String> getRolesByEmpId(String empId) {
		return employeeDao.getRolesByEmpId(empId);
	}
	
	@Override
	public String findEmpId(Employee employee) {
		//아이디 찾기
		
		//입력된 이름과 이메일로 아이디 정보 찾기
		String id = employeeDao.selectEmpIdByNameEmail(employee);
		if(id == null) {
			return "존재하지 않는 정보입니다";
		} 
		
		//존재하면 해당 이메일로 아이디 정보 보내기
		String mailSubject = "사원 id 안내입니다.";
		String mailMessage = "해당 사원의 id 정보는 다음과 같습니다.";
		String result = sendMail(id, employee.getEmail(), mailSubject, mailMessage);
		return result;
	}
	
	@Override
	public String sendMail(String content, String email, String mailSubject, String mailMessage){
    	//메일 전송 성공 여부
    	boolean success = false;
        
        //메일 내용 작성
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
        	message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject(mailSubject);
	        String body = "";
	        body += "<h3>" + mailMessage + "</h3>";
	        body += "<h1>" + content + "</h1>";
	        message.setText(body,"UTF-8", "html");
	        
	        //메일 전송
	        javaMailSender.send(message);
	        
	        success = true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
        
        if(success) {
        	return "메일 전송 성공";
        } else {
        	return "메일 전송 실패";
        }
    }
	
	@Override
	public Map<String, String> findPassword(Employee employee) {
		//비밀번호 찾기
		Map<String, String> resultMap = new HashMap<>();
		//입력된 정보로 사원 정보 찾기
		Employee emp = employeeDao.selectEmployeeInfoByIdNameEmail(employee);
		if(emp == null) {
			resultMap.put("message", "존재하지 않는 정보입니다.");
			return resultMap;
		}
		
		//존재하면 랜덤코드 생성 후 해당 이메일로 인증코드 발송
		//랜덤 인증코드 6자리 생성
    	Random random = new Random();
        StringBuffer authCode = new StringBuffer();
        while(authCode.length()<6){
            if(random.nextBoolean()){
            	authCode.append((char)((int)(random.nextInt(26))+65));
            }
            else{
            	authCode.append(random.nextInt(10));
            }                
        }
        String mailSubject = "휴가관리 이메일 인증";
		String mailMessage = "요청하신 인증 번호입니다.";
		String result = sendMail(authCode.toString(), employee.getEmail(), mailSubject, mailMessage);
		resultMap.put("message", result);
		resultMap.put("authCode", authCode.toString());
		return resultMap;
	}
	
	@Override
	public String changePassword(Employee employee) {
		// 비밀번호 변경
		int result = employeeDao.updatePassword(employee);
		if(result != 1) {
			return "비밀번호 변경 실패";
		}
		else {
			return "비밀번호가 변경되었습니다.";
		}
	}

	@Override
	public VacationApprovalWaiting[] searchVacationApprovalWaiting(String empId) {
		return employeeDao.searchVacationApprovalWaiting(empId, "결재대기", "팀장");
	}

	@Override
	public List<EmployeeVacationCountPerMonth> vacationCountPerMonth(String empId) {
		List<EmployeeVacationCountPerMonth> monthVacationCountInformations = new ArrayList<>();
		List<String> monthList = previousSixMonthsList();
		for (String monthText: monthList) {
			EmployeeVacationCountPerMonth employeeVacationCountPerMonth = new EmployeeVacationCountPerMonth();
			employeeVacationCountPerMonth.setMonth(monthText);
			monthVacationCountInformations.add(employeeVacationCountPerMonth);
		}
		EmployeeVacationCountPerMonthResponseDTO[] vacationInformations = employeeDao.searchEmployeeVacationCountPerMonth(empId);
		for (EmployeeVacationCountPerMonthResponseDTO vacationInformation: vacationInformations) {
			String startMonth = vacationInformation.getStartMonth();
			int startMonthCount = vacationInformation.getStartMonthDays();
			String endMonth = vacationInformation.getEndMonth();
			int endMonthCount = vacationInformation.getEndMonthDays();
	        for (EmployeeVacationCountPerMonth item : monthVacationCountInformations) {
	            if (item.getMonth().equals(startMonth)) {
	                item.setMonthPerCount(item.getMonthPerCount()+startMonthCount);
	            }
	            if (item.getMonth().equals(endMonth)) {
	                item.setMonthPerCount(item.getMonthPerCount()+endMonthCount);
	            }
	        }
		}
		return monthVacationCountInformations;
	}
	
	
	List<String> previousSixMonthsList() {
        // 현재 날짜를 가져옴
        LocalDate currentDate = LocalDate.now();
        // 날짜 포매터를 사용하여 '월' 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월");
        String currentMonth = currentDate.format(formatter);
        
        List<String> monthList = new ArrayList<>();
        
        switch (currentMonth) {
		case "1월":
			monthList.add("8월");
			monthList.add("9월");
			monthList.add("10월");
			monthList.add("11월");
			monthList.add("12월");
			break;
		case "2월":
			monthList.add("9월");
			monthList.add("10월");
			monthList.add("11월");
			monthList.add("12월");
			monthList.add("1월");
			break;
		case "3월":
			monthList.add("10월");
			monthList.add("11월");
			monthList.add("12월");
			monthList.add("1월");
			monthList.add("2월");
			break;
		case "4월":
			monthList.add("11월");
			monthList.add("12월");
			monthList.add("1월");
			monthList.add("2월");
			monthList.add("3월");
			break;
		case "5월":
			monthList.add("12월");
			monthList.add("1월");
			monthList.add("2월");
			monthList.add("3월");
			monthList.add("4월");
			break;
		case "6월":
			monthList.add("1월");
			monthList.add("2월");
			monthList.add("3월");
			monthList.add("4월");
			monthList.add("5월");
			break;
		case "7월":
			monthList.add("2월");
			monthList.add("3월");
			monthList.add("4월");
			monthList.add("5월");
			monthList.add("6월");
			break;
		case "8월":
			monthList.add("3월");
			monthList.add("4월");
			monthList.add("5월");
			monthList.add("6월");
			monthList.add("7월");
			break;
		case "9월":
			monthList.add("4월");
			monthList.add("5월");
			monthList.add("6월");
			monthList.add("7월");
			monthList.add("8월");
			break;
		case "10월":
			monthList.add("5월");
			monthList.add("6월");
			monthList.add("7월");
			monthList.add("8월");
			monthList.add("9월");
			break;
		case "11월":
			monthList.add("6월");
			monthList.add("7월");
			monthList.add("8월");
			monthList.add("9월");
			monthList.add("10월");
			break;
		case "12월":
			monthList.add("7월");
			monthList.add("8월");
			monthList.add("9월");
			monthList.add("10월");
			monthList.add("11월");
			break;
			
		default:
			break;
		}
		monthList.add(currentMonth);
        return monthList;
	}
	

}
