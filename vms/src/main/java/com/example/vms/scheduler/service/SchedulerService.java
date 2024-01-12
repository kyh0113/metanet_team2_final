package com.example.vms.scheduler.service;

import java.sql.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.repository.ICertificateRepository;
import com.example.vms.schedule.repository.IScheduleRepository;
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

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void certificateScheduler() {
        log.info("certificate 스케줄러 발동");

        Date currentDate = new Date(System.currentTimeMillis());

        Certificate[] certificates = certificateRepository.searchCertificates();
        for (Certificate certificate : certificates) {
        	long dayDifference = (currentDate.getTime() - certificate.getRegDate().getTime())/ (24 * 60 * 60 * 1000);
            if (dayDifference>30) {
                log.info("삭제된 certificate: " + certificate.toString());
            	certificateRepository.deleteCertificate(certificate.getCertificateId());
            }
            
        }
    }
    
    @Scheduled(cron="0 0 0 1 12 ?") // 매년 12월 1일 자정
    public void vacationPromoEmail() { // 1개 이상의 연차를 가진 직원에게 연차 촉진 메일을 자동으로 발송
    	log.info("vacationPromoEmail 스케줄러 발동");
    	// SELECT * FROM employees WHERE remains >= 1;
    	
    }  

	@Override
	public SchedulerResult[] searchSchedulers(int start, int end, String content, int success) {
		return schedulerRepository.searchSchedulers(start, end, content, success);
	}

}
