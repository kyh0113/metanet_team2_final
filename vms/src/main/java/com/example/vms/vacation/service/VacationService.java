package com.example.vms.vacation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.repository.IVacationRepository;
import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.vacation.model.VacationEmployee;

@Service
public class VacationService implements IVacationService{
   
   @Autowired
   IVacationRepository vacationDao;

   @Autowired
	IEmployeeRepository employeeRepository;

   @Override
   public void requestVacation(Vacation vacation) {
	   
	  Integer typeId = vacation.getTypeId(); // 폼에서 받아온 값
	  vacation.setTypeId(typeId);
      vacation.setRegId(vacationDao.maxRegId()+1);
      vacationDao.requestVacation(vacation);
   }

   @Override
   public int maxRegId() {
      vacationDao.maxRegId();
      return 0;
   }

   @Override
	public List<VacationEmployee> getDeptRequestList(String empId) {
		return vacationDao.selectRequestListByDept(empId);
	}

	@Override
	public Vacation getRequestDetail(int regId) {
		return vacationDao.selectRequestByRegId(regId);
	}

	@Override
	public String approvalRequest(Vacation vacation) {
		int result = vacationDao.updateRequest(vacation);
		if(result == 1) {
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

}
