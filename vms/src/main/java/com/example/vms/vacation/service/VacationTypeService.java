package com.example.vms.vacation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.vacation.model.VacationType;
import com.example.vms.vacation.repository.IVacationTypeRepository;

@Service
public class VacationTypeService implements IVacationTypeService{

	@Autowired
	IVacationTypeRepository vacationTypeDao;
	
	@Override
	public List<VacationType> getAllVacationType() {
		return vacationTypeDao.getAllVacationType();
	}

}
