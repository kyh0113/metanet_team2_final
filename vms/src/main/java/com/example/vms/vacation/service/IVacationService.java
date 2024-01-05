package com.example.vms.vacation.service;

import com.example.vms.vacation.model.Vacation;

public interface IVacationService {
   void requestVacation(Vacation vacation);
   public int maxRegId();
}
