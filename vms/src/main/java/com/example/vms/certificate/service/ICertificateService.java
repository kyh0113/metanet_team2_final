package com.example.vms.certificate.service;

import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;

public interface ICertificateService {

	public int createCertificate(CertificateRequestDTO certificateDTO);
	
	public CertificateResponseDTO[] searchCertificatesByEmpId(String empId);
	public CertificateResponseDTO[] searchCertificatesByCertificateId(int certificateId);
}
