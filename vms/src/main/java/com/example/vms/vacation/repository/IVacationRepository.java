package com.example.vms.vacation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

@Repository
@Mapper
public interface IVacationRepository {
	
	//팀원 휴가 신청서 조회
	List<VacationEmployee> selectRequestListByDept(String empId);
	Vacation selectRequestByRegId(int regId);
	//휴가 결재
	int updateRequest(Vacation vacation);

}
