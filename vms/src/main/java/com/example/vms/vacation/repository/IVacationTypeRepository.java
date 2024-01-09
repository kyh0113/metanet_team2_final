package com.example.vms.vacation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.VacationType;

@Repository
@Mapper
public interface IVacationTypeRepository {
	List<VacationType> getAllVacationType();
	int findDaysByTypeId(int typeId);
}
