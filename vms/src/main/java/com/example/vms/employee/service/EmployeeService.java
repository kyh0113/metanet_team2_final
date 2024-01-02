package com.example.vms.employee.service;

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
	
}
