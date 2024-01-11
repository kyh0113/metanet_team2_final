package com.example.vms.scheduler.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import com.example.vms.manager.model.Employee;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.scheduler.service.SchedulerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SchedulerController {

	@Autowired
	private SchedulerService schedulerService;

	@Autowired
	private ManagerService managerService;

	@Scheduled(cron = "0 0 0 1 12 ?") // 매년 12월 1일 자정
	public void vacationPromoEmail() { // 1개 이상의 연차를 가진 직원에게 연차 촉진 메일을 자동으로 발송
		log.info("vacationPromoEmail 스케줄러 발동");
		// SELECT * FROM employees WHERE remains >= 1;

	}

////	@Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
//	@Scheduled(cron = "*/20 * * * * *")
//	public void grantVacation() {
//	    List<Employee> employees = managerService.getAllEmployees();
//	    int currentMonth = LocalDate.now().getMonthValue();
//
//	    for (Employee employee : employees) {
//	        LocalDate hireDate = employee.getHireDate();
//
//	        // 몇 달 일했는지 나왔음.
//	        long monthsSinceHire = ChronoUnit.MONTHS.between(hireDate, LocalDate.now());
//	        System.out.println("monthsSinceHire: "+monthsSinceHire);
//	        int currentRemains = employee.getRemains();
//
//	        if (monthsSinceHire < 12) { // 1년 미만 근무자
//	            employee.setRemains(currentRemains + 1);
//	        } else if (monthsSinceHire >= 12 && currentMonth == 1) { // 1년 이상 근무자이며 올해가 1월인 경우
//	            int remainingVacation = (int) Math.min(15 - currentRemains, monthsSinceHire);
//	            employee.setRemains(currentRemains + remainingVacation);
//	        }
//
//	        managerService.updateRemains(employee.getEmpId(), employee.getRemains());
//	        log.info("연차 부여: Employee ID={}, 현재 연차={}", employee.getEmpId(), employee.getRemains());
//	    }
//	}
//
//	@Scheduled(cron = "0 0 0 1 1 ?") // 1월 1일에 실행
//	public void grantVacation2() {
//	    List<Employee> employees = managerService.getAllEmployees();
//
//	    for (Employee employee : employees) {
//	        LocalDate hireDate = employee.getHireDate();
//	        long yearsSinceHire = ChronoUnit.YEARS.between(hireDate, LocalDate.now());
//	        int currentRemains = employee.getRemains();
//
//	        // 몇 달 일했는지 나왔음.
//	        long monthsSinceHire = ChronoUnit.MONTHS.between(hireDate, LocalDate.now());
//	        System.out.println("monthsSinceHire: "+monthsSinceHire);
//	        if (yearsSinceHire >= 1 && currentRemains < 15) { // 1년 이상 다녀간 경우이며 아직 15개 미만의 연차를 받은 경우
//	            int remainingVacation = (int) Math.min(15 - currentRemains, monthsSinceHire - 12);
//	            employee.setRemains(currentRemains + remainingVacation);
//	        }
//
//	        managerService.updateRemains(employee.getEmpId(), employee.getRemains());
//	        log.info("연차 부여: Employee ID={}, 현재 연차={}", employee.getEmpId(), employee.getRemains());
//	    }
//	}


}
