package com.example.vms.certificate.repository;


import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.certificate.model.Certificate;

@Repository
@Mapper
public interface ICertificateRepository {

	public void createCertificate(
			Certificate certificate
	);
	public int searchMaxCertificateId();
	public int searchCertificateIdByEmpId(
			@Param("empId") String empId
	);

}
