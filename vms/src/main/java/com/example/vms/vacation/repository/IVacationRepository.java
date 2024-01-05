package com.example.vms.vacation.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.Vacation;

@Repository
@Mapper
public interface IVacationRepository {
   void requestVacation(Vacation vacation);
   public int maxRegId();
}
