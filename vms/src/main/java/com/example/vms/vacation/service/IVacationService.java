package com.example.vms.vacation.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.schedule.model.ScheduleExcel;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

public interface IVacationService {
   void requestVacation(Vacation vacation, MultipartFile[] files);
   public int maxRegId();

   int getCountDeptRequestList(String empId, String state);
   List<VacationEmployee> getDeptRequestList(String empId, String state, String curPage);
   Vacation getRequestDetail(int regId);
   String approvalRequest(Vacation vacation);
   String getDeptNameByEmpId(String empId);
   String getVacationTypeName(int typeId);
   List<UploadFile> getFileList(int regId);
   UploadFile getFile(int fileId);
   int getCountRequestList(String empId, String state);
   List<VacationEmployee> getRequestList(String empId, String state, String curPage);
   void deleteVacation(int regId);
   int getCountScheduleByOption(String keyword, int option);
   List<ScheduleEmpDeptType> getScheduleListByOption(String curPage, String keyword, int option);
   List<ScheduleExcel> selectAllVacationByDept(int deptId);
}