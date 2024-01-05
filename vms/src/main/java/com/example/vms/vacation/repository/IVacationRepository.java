package com.example.vms.vacation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;

@Repository
@Mapper
public interface IVacationRepository {
   void requestVacation(Vacation vacation);
   public int maxRegId();
	
	List<VacationEmployee> selectRequestListByDept(String empId);
	Vacation selectRequestByRegId(int regId);
	int updateRequest(Vacation vacation);
}
