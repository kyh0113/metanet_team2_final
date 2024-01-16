package com.example.vms.scheduler.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.repository.ICertificateRepository;
import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.schedule.repository.IScheduleRepository;
import com.example.vms.scheduler.model.Scheduler;
import com.example.vms.scheduler.model.SchedulerResult;
import com.example.vms.scheduler.repository.ISchedulerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SchedulerService implements ISchedulerService{
	
	@Autowired
	private ISchedulerRepository schedulerDao;

    @Autowired
    private ICertificateRepository certificateRepository;
    
    @Autowired
    private ISchedulerRepository schedulerRepository;
    
    @Autowired
    private IEmployeeService employeeService;

    @Transactional
    public void certificateScheduler() {
        log.info("certificate 스케줄러 발동");

        Date currentDate = new Date(System.currentTimeMillis());

        Certificate[] certificates = certificateRepository.searchCertificates();
        try {
            for (Certificate certificate : certificates) {
            	long dayDifference = (currentDate.getTime() - certificate.getRegDate().getTime())/ (24 * 60 * 60 * 1000);
                if (dayDifference>30) {
                    log.info("삭제된 certificate: " + certificate.toString());
                	certificateRepository.deleteCertificate(certificate.getCertificateId());
                }
                
            }
			Scheduler scheduler = new Scheduler();
			scheduler.setSchedulerId(schedulerDao.maxSchedulerId() + 1);
			scheduler.setWorkDate(LocalDateTime.now());
			scheduler.setContent("certificate 스케줄러 성공");
			scheduler.setSuccess(1); // 성공
			saveScheduler(scheduler);
        } catch (Exception e) {
			Scheduler scheduler = new Scheduler();
			scheduler.setSchedulerId(schedulerDao.maxSchedulerId() + 1);
			scheduler.setWorkDate(LocalDateTime.now());
			scheduler.setContent("certificate 스케줄러 성공 실패");
			scheduler.setSuccess(0); // 실패
			saveScheduler(scheduler);
        }
    }
    
	@Override
	public SchedulerResult[] searchSchedulers(int start, int end, String content, int success) {
		return schedulerRepository.searchSchedulers(start, end, content, success);
	}

	@Override
	public void saveScheduler(Scheduler scheduler) {
		schedulerDao.saveScheduler(scheduler);
	}

	@Override
	public int maxSchedulerId() {
		return schedulerDao.maxSchedulerId();
	}

	
	@Async
	@Transactional
	public void sendVacationPromoEmail(Employee employee) {
		// 메일 내용 작성
		String mailContent = "남은 연차: " + employee.getRemains() + "일이 남았습니다.";
		String mailSubject = "연차 촉진 알림";
		String mailMessage = "연차 촉진 알림";

		String result = employeeService.sendMail(mailContent, employee.getEmail(), mailSubject, mailMessage);

		System.out.println("메일 전송 결과: " + result);

	}
	

}
