package com.example.vms.vacation.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;

@Service
public class VacationService implements IVacationService {

	@Autowired
	IVacationRepository vacationDao;

	@Autowired
	IUploadFileRepository uploadFileDao;

	@Autowired
	IEmployeeRepository employeeRepository;

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
		if (result == 1) {
			return "결재 완료";
		} else {
			return "결재 실패";
		}
	}

	@Override
	public String getDeptNameByEmpId(String empId) {
		return employeeRepository.selectDeptNameByEmpId(empId);
	}

}
