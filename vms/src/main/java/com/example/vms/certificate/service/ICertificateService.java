package com.example.vms.certificate.service;

import java.sql.Date;

import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;

public interface ICertificateService {

	public String createCertificate(CertificateRequestDTO certificateDTO);
	
	public CertificateResponseDTO[] searchCertificatesByEmpId(String empId);
	public CertificateResponseDTO[] searchCertificatesByCertificateId(String certificateId);
	public Date currentTime();
}
