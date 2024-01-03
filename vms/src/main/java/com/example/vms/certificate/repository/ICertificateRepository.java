package com.example.vms.certificate.repository;


import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.model.CertificateResponseDTO;

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
	public CertificateResponseDTO[] searchCertificatesByEmpId(
		@Param("empId") String empId
	);
	public void deleteCertificate(
		@Param("certificateId") int certificateId
	);

}
