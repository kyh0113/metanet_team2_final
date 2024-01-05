package com.example.vms.certificate.service;


import java.sql.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;
import com.example.vms.certificate.repository.ICertificateRepository;

@Service
public class CertificateService implements ICertificateService {

	@Autowired
	ICertificateRepository certificateRepository;
	
	@Override
	@Transactional
	public String createCertificate(CertificateRequestDTO certificateDTO) {
		
    	Certificate certificate = new Certificate();
    	certificate.setCertificateId(UUID.randomUUID().toString());
    	certificate.setRegDate(currentTime());
    	certificate.setType(certificateDTO.getType());
    	certificate.setEmpId(certificateDTO.getEmp_id());
		try {
	    	CertificateResponseDTO[] certificates = certificateRepository.searchCertificatesByEmpId(certificateDTO.getEmp_id());
	    	
	    	for (CertificateResponseDTO cert: certificates) {
	    		if (cert.getType().equals(certificate.getType())) {
	    			certificateRepository.deleteCertificate(cert.getCertificateId());
	    		}
	    	}
			certificateRepository.createCertificate(
					certificate
				);
			return certificate.getCertificateId();
		} catch (Exception e) {
			System.out.println(e);
			return null; 
		}
	}

	@Override
	public CertificateResponseDTO[] searchCertificatesByEmpId(String empId) {
		return certificateRepository.searchCertificatesByEmpId(empId);
	}

	@Override
	public CertificateResponseDTO[] searchCertificatesByCertificateId(String certificateId) {
		return certificateRepository.searchCertificatesByCertificateId(certificateId);
	}
	
	public Date currentTime() {
        // 현재 날짜와 시간 얻기
        java.util.Date currentDate = new java.util.Date();
        // java.util.Date를 java.sql.Date로 변환
        Date sqlDate = new Date(currentDate.getTime());
        return sqlDate;
	}

	@Override
	public String searchDepartNameByDeptId(int deptId) {
		return certificateRepository.searchDepartNameByDeptId(deptId);
	}

}
