package com.example.vms.vacation.service;

import java.util.List;

import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

public interface IVacationService {

	List<VacationEmployee> getDeptRequestList(String empId);
	Vacation getRequestDetail(int regId);
	String approvalRequest(Vacation vacation);
	String getDeptNameByEmpId(String empId);
}
