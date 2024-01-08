package com.example.vms.vacation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.repository.IVacationRepository;

@Service
public class VacationService implements IVacationService{
   
   @Autowired
   IVacationRepository vacationDao;

   @Autowired
   IEmployeeRepository employeeRepository;
   
   @Autowired
   EmployeeService employeeService;

   @Override
   public void requestVacation(Vacation vacation) {
      vacation.setRegId(vacationDao.maxRegId()+1);
      vacationDao.requestVacation(vacation);
   }

   @Override
   public int maxRegId() {
      vacationDao.maxRegId();
      return 0;
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
		}
		else {
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
	public List<String> getFileList(int regId) {
		return vacationDao.selectFileListByRegId(regId);
	}
	
	@Override
	public UploadFile getFile(int fileId) {
		return vacationDao.selectFile(fileId);
	}

}
