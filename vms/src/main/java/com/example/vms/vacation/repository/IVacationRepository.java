package com.example.vms.vacation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.model.VacationVacationType;

@Repository
@Mapper
public interface IVacationRepository {
	void requestVacation(Vacation vacation);
	public int maxRegId();

	int selectCountRequestListByDept(@Param("empId") String empId, @Param("state") String state);
	List<VacationEmployee> selectRequestListByDept(@Param("empId") String empId, @Param("state") String state, @Param("startNum") int startNum, @Param("endNum") int endNum );
	Vacation selectRequestByRegId(int regId);
	int updateRequest(Vacation vacation);
	String selectTypeName(int typeId);
	int selectCountRequestListByEmpId(@Param("empId") String empId, @Param("state") String state);
	List<VacationVacationType> selectRequestListByEmpId(@Param("empId") String empId, @Param("state") String state, @Param("startNum") int startNum, @Param("endNum") int endNum );
	//List<String> selectFileListByRegId(int regId);
	//UploadFile selectFile(int fileId);
}
