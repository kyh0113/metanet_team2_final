package com.example.vms.employee.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}
