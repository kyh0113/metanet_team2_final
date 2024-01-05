package com.example.vms.certificate.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.example.vms.certificate.model.CertificateResponseDTO;

@Service 
public class DataMapper {
	public Context setData(CertificateResponseDTO certificateResponseDTO) {
		Context context = new Context();
		Map<String, Object> data = new HashMap<>();
		data.put("certificate", certificateResponseDTO);
		context.setVariables(data);
		return context;
	}
}