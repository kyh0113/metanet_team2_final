package com.example.vms.vacation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.repository.IVacationRepository;

@Service
public class VacationService implements IVacationService{
   
   @Autowired
   IVacationRepository vacationDao;

   @Override
   public void requestVacation(Vacation vacation) {
      vacation.setRegId(vacationDao.maxRegId()+1);
      vacationDao.requestVacation(vacation);
   }

   @Override
   public int maxRegId() {
      vacationDao.maxRegId();
      return 0;
   }

}
