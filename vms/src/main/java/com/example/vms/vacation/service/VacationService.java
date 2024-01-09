package com.example.vms.vacation.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.employee.service.EmployeeService;

import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;

@Service
public class VacationService implements IVacationService {

   @Autowired
   IEmployeeRepository employeeRepository;
   
   @Autowired
   EmployeeService employeeService;

	@Autowired
	IVacationRepository vacationDao;

	@Autowired
	IUploadFileRepository uploadFileDao;

	@Override
	public void requestVacation(Vacation vacation, MultipartFile[] files) {
		Integer typeId = vacation.getTypeId(); // 폼에서 받아온 값
		vacation.setTypeId(typeId);
		vacation.setRegId(vacationDao.maxRegId() + 1);
		vacationDao.requestVacation(vacation);

		for (MultipartFile file : files) {
			UploadFile uploadFile = new UploadFile(); // 매번 새로운 객체 생성
			uploadFile.setFileId(uploadFileDao.maxFileId() + 1);
			uploadFile.setRegId(vacation.getRegId());
			uploadFile.setName(file.getOriginalFilename());
			uploadFile.setFileSize(file.getSize());
			uploadFile.setContentType(file.getContentType());
			try {
				uploadFile.setFileData(file.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 파일 정보 설정 및 저장 로직
			uploadFileDao.insertUploadFile(uploadFile);
		}
	}

	@Override
	public int maxRegId() {
		return vacationDao.maxRegId();
	}
  
   @Override
	public List<VacationEmployee> getDeptRequestList(String empId, String state, String curPage) {
	 //int rownum = vacationDao.selectCountRequestListByDept(empId, state);
	   int curPageNum = Integer.parseInt(curPage);
	   int startNum = curPageNum*10 - 9;
	   int endNum = curPageNum*10;
	   return vacationDao.selectRequestListByDept(empId, state, startNum, endNum);
	}
	@Override
	public Vacation getRequestDetail(int regId) {
		return vacationDao.selectRequestByRegId(regId);
	}

	@Override
	public String approvalRequest(Vacation vacation) {
		int result = vacationDao.updateRequest(vacation);

		if(result == 1) {
			//알림 메일 전송
			//사원 이메일 검색
//			Vacation requestVacation = vacationDao.selectRequestByRegId(vacation.getRegId());
//			Employee employee = employeeRepository.selectEmployee(requestVacation.getEmpId());
//			String email = employee.getEmail();
//			String mailSubject = "결재 알림입니다.";
//			String mailMessage = "요청하신 휴가 결재 결과입니다.";
//			StringBuffer content = new StringBuffer();
//			content.append(vacation.getRegId()).append(" ");
//			content.append(requestVacation.getTypeId()).append(" ");
//			content.append(requestVacation.getStartDate()).append("~").append(requestVacation.getEndDate()).append(" ");
//			content.append(requestVacation.getState());
//			
//			employeeService.sendMail(content.toString(), email, mailSubject, mailMessage);
			return "결재 완료";
		} else {
			return "결재 실패";
		}
	}

	@Override
	public String getDeptNameByEmpId(String empId) {
		return employeeRepository.selectDeptNameByEmpId(empId);
	}

	@Override
	public int getCountDeptRequestList(String empId, String state) {
		return vacationDao.selectCountRequestListByDept(empId, state);
	}

	@Override
	public String getVacationTypeName(int typeId) {
		return vacationDao.selectTypeName(typeId);
	}

	@Override
	public List<UploadFile> getFileList(int regId) {
		return uploadFileDao.selectFileListByRegId(regId);
	}
	
	@Override
	public UploadFile getFile(int fileId) {
		return uploadFileDao.selectFile(fileId);
	}

}
