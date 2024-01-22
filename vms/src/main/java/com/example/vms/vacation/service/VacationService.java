package com.example.vms.vacation.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.schedule.model.ScheduleExcel;
import com.example.vms.schedule.repository.IScheduleRepository;

import com.example.vms.schedule.service.ScheduleService;
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

	@Autowired
	IScheduleRepository scheduleDao;

	@Autowired
	ScheduleService scheduleservice;

	@Override
	@Transactional
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
		int curPageNum = Integer.parseInt(curPage);
		int startNum = curPageNum * 10 - 9;
		int endNum = curPageNum * 10;
		return vacationDao.selectRequestListByDept(empId, state, startNum, endNum);
	}

	@Override
	public Vacation getRequestDetail(int regId) {
		return vacationDao.selectRequestByRegId(regId);
	}

	@Override
	@Transactional
	public String approvalRequest(Vacation vacation) {
		int result = vacationDao.updateRequest(vacation);
		
		if (result == 1) {
			Vacation realvacation = vacationDao.selectRequestByRegId(vacation.getRegId());
			
			if (realvacation != null) {
				System.out.println("신청한 휴가 상태: "+realvacation.getState());
				if(realvacation.getState().equals("승인")) {
					Schedule schedule = new Schedule();
					schedule.setCalender_Id(scheduleservice.maxScheduleId() + 1);
					schedule.setReg_id(realvacation.getRegId());
					schedule.setDept_id(employeeService.selectEmployee(realvacation.getEmpId()).getDeptId());
					schedule.setEmp_id(realvacation.getEmpId());
					schedule.setEnd_date(realvacation.getEndDate().plusDays(1));  // 달력 막대에 종료 날짜는 포함이 안되서 임의로 +1
					schedule.setStart_date(realvacation.getStartDate());
					schedule.setTitle("[" + getVacationTypeName(realvacation.getTypeId()) + "] "
							+ employeeService.selectEmployee(realvacation.getEmpId()).getName());
					schedule.setType_id(realvacation.getTypeId());

					scheduleservice.insertSchedule(schedule);
				} 
			} else {
			    return "신청서 정보 없음";
			}
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

	@Override
	public int getCountRequestList(String empId, String state) {
		return vacationDao.selectCountRequestListByEmpId(empId, state);
	}

	@Override
	public List<VacationEmployee> getRequestList(String empId, String state, String curPage) {
		int curPageNum = Integer.parseInt(curPage);
		int startNum = curPageNum * 10 - 9;
		int endNum = curPageNum * 10;
		return vacationDao.selectRequestListByEmpId(empId, state, startNum, endNum);
	}

	@Override
	public List<ScheduleEmpDeptType> getScheduleListByOption(String curPage, String keyword, int option) {
		int curPageNum = Integer.parseInt(curPage);
		int startNum = curPageNum * 10 - 9;
		int endNum = curPageNum * 10;
		List<ScheduleEmpDeptType> scheduleList;
		switch (option) {
		case 1: // 사원명
			scheduleList = scheduleDao.getScheduleListByEmpName(startNum, endNum, keyword);
			break;
		case 2: // 부서
			scheduleList = scheduleDao.getScheduleListByDeptName(startNum, endNum, keyword);
			break;
		case 3: // 직위
			scheduleList = scheduleDao.getScheduleListByPosition(startNum, endNum, keyword);
			break;
		default: // 전체
			scheduleList = scheduleDao.getAllScheduleList(startNum, endNum);
			break;
		}
		return scheduleList;
	}

	@Override
	public int getCountScheduleByOption(String keyword, int option) {
		int rowNum = 0;
		switch (option) {
		case 1: // 사원명
			rowNum = scheduleDao.getCountScheduleByEmpName(keyword);
			break;
		case 2: // 부서
			rowNum = scheduleDao.getCountScheduleByDeptName(keyword);
			break;
		case 3: // 직위
			rowNum = scheduleDao.getCountScheduleByPosition(keyword);
			break;
		default: // 전체
			rowNum = scheduleDao.getCountAllSchedule();
			break;
		}
		return rowNum;
	}

	@Override
	@Transactional
	public void deleteVacation(int regId) {
		vacationDao.deleteVacation(regId);
	}
	

}