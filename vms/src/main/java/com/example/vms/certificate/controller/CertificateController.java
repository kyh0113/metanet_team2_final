package com.example.vms.certificate.controller;

import java.util.HashSet;
import java.util.Set;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;
import com.example.vms.certificate.repository.ICertificateRepository;
import com.example.vms.certificate.service.ICertificateService;
import com.example.vms.employee.controller.EmployeeController;

import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController 
@RequestMapping("/certificate")
public class CertificateController {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	
	@Autowired
	ICertificateService certificateService;

    @GetMapping("")
    public String getMethodName() {
        return "certificate/home";
    }
    
    @PostMapping("/generate")
    public int generateCerti(
    	@RequestBody CertificateRequestDTO certificateRequestDTO
    ) {
    	return certificateService.createCertificate(certificateRequestDTO);
    }
    
    @GetMapping("/search") 
    public CertificateResponseDTO[] searchCertificatesByEmpId(
    	@RequestParam(name = "emp_id", required=false, defaultValue = "") String emp_id,
    	@RequestParam(name = "certificate_id", required = false) Integer certificate_id
    ) {
    	if (certificate_id != null) {
    		return certificateService.searchCertificatesByCertificateId(certificate_id);
    	}
    	return certificateService.searchCertificatesByEmpId(emp_id);
    }
    
}
