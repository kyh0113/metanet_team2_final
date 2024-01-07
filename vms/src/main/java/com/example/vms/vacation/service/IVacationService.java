package com.example.vms.vacation.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

public interface IVacationService {
	void requestVacation(Vacation vacation, MultipartFile[] files);
	public int maxRegId();

	List<VacationEmployee> getDeptRequestList(String empId);
	Vacation getRequestDetail(int regId);
	String approvalRequest(Vacation vacation);
	String getDeptNameByEmpId(String empId);
}
