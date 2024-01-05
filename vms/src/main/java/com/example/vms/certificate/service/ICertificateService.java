package com.example.vms.certificate.service;

import java.sql.Date;

import org.apache.ibatis.annotations.Param;

import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;

public interface ICertificateService {

	public String createCertificate(CertificateRequestDTO certificateDTO);
	
	public CertificateResponseDTO[] searchCertificatesByEmpId(String empId);
	public CertificateResponseDTO[] searchCertificatesByCertificateId(String certificateId);
	public Date currentTime();
	public String searchDepartNameByDeptId(int deptId);
}
