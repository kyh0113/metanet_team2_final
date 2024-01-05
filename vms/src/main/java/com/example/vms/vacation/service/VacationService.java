package com.example.vms.vacation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myapp.member.dao.IMemberRepository;
import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.repository.IVacationRepository;

@Service
public class VacationService implements IVacationService {

	@Autowired
	IVacationRepository vacationRepository;
	@Autowired
	IEmployeeRepository employeeRepository;
	
	@Override
	public List<VacationEmployee> getDeptRequestList(String empId) {
		return vacationRepository.selectRequestListByDept(empId);
	}

	@Override
	public Vacation getRequestDetail(int regId) {
		return vacationRepository.selectRequestByRegId(regId);
	}

	@Override
	public String approvalRequest(Vacation vacation) {
		int result = vacationRepository.updateRequest(vacation);
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
