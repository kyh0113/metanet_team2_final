package com.example.vms.scheduler.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.IManagerService;
import com.example.vms.scheduler.model.SchedulerResult;
import com.example.vms.scheduler.service.ISchedulerService;
import com.example.vms.scheduler.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/scheduler")
public class SchedulerController {

   @Autowired
   ISchedulerService schedulerService;
   
   @Autowired
   IManagerService managerService;
   
   @Autowired
   IEmployeeService employeeService;
   
   @GetMapping("/list")
   @ResponseBody
   public SchedulerResult[] searchSchedulers(
      @RequestParam(name = "start", defaultValue = "0") int start,
      @RequestParam(name = "end", defaultValue = "10") int end, 
      @RequestParam(name = "success", defaultValue = "3", required = false) int success,
      @RequestParam(name = "content", defaultValue = "", required = false) String content 
   ) {
      SchedulerResult[] schedulers = null;
      System.out.println(start + " 시작 " + end + " 끝 ");
      schedulers = schedulerService.searchSchedulers(start, end, content, success);
      return schedulers;
   }
   
   // 스케줄러 목록 페이지 
   @GetMapping("/list/view")
   public String searchSchedulerPage() {
      return "/scheduler/list";
   }
   
   	@Scheduled(cron = "0 0 0 1 12 ?") // 매년 12월 1일 자정
	public void vacationPromoEmail() { // 1개 이상의 연차를 가진 직원에게 연차 촉진 메일을 자동으로 발송
		log.info("vacationPromoEmail 스케줄러 발동");
		List<Employee> employees = managerService.findEmployeesWithAtLeastOneVacation();
		
		for(Employee employee : employees) {
			System.out.println(employee.getEmail());
			sendVacationPromoEmail(employee);
		}
   }
   
   public void sendVacationPromoEmail(Employee employee) {
	   // 메일 내용 작성
	   String mailContent = "남은 연차: "+ employee.getRemains()+"일이 남았습니다.";
	   String mailSubject = "연차 촉진 알림";
	   String mailMessage = "연차 촉진 알림";
	   
	   String result = employeeService.sendMail(mailContent, employee.getEmail(), mailSubject, mailMessage);
		
	   System.out.println("메일 전송 결과: "+result);	
		
	}

	@Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
	public void grantVacation() {
	    List<Employee> employees = managerService.getAllEmployees();
	    int currentMonth = LocalDate.now().getMonthValue();

	    for (Employee employee : employees) {
	        LocalDate hireDate = employee.getHireDate();

	        // 몇 달 일했는지 나왔음.
	        long monthsSinceHire = ChronoUnit.MONTHS.between(hireDate, LocalDate.now());
	        System.out.println("monthsSinceHire: "+monthsSinceHire);
	        int currentRemains = employee.getRemains();

	        if (monthsSinceHire < 12) { // 1년 미만 근무자
	            employee.setRemains(currentRemains + 1);
	        }else if(12 == monthsSinceHire && currentMonth !=1) {
	        	 int remainingVacation = 15 - currentMonth-1;
	        }
	        managerService.updateRemains(employee.getEmpId(), employee.getRemains());
	        log.info("연차 부여: Employee ID={}, 현재 연차={}", employee.getEmpId(), employee.getRemains());
	    }
	}

	@Scheduled(cron = "0 0 0 1 1 ?") // 1월 1일에 실행
	public void grantVacation2() {
	    List<Employee> employees = managerService.getAllEmployees();
	    int currentMonth = LocalDate.now().getMonthValue();
	    
	    for (Employee employee : employees) {
	        LocalDate hireDate = employee.getHireDate();
	        long yearsSinceHire = ChronoUnit.YEARS.between(hireDate, LocalDate.now());
	        int currentRemains = employee.getRemains();

	        // 몇 달 일했는지 나왔음.
	        long monthsSinceHire = ChronoUnit.MONTHS.between(hireDate, LocalDate.now());
	        if (monthsSinceHire > 12 && currentMonth ==1) { // 1년 이상 다녀간 경우이며 아직 15개 미만의 연차를 받은 경우
	            int remainingVacation = 15;
	            employee.setRemains(currentRemains + remainingVacation);
	        }

	        managerService.updateRemains(employee.getEmpId(), employee.getRemains());
	        log.info("연차 부여: Employee ID={}, 현재 연차={}", employee.getEmpId(), employee.getRemains());
	    }
	}
   
}