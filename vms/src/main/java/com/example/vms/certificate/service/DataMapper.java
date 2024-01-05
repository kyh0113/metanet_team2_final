package com.example.vms.certificate.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.example.vms.certificate.model.CertificateResponseDTO;

@Service 
public class DataMapper {
	
	@Autowired
	QRCodeGenerator qrCodeGenerator;
	
	public Context setData(CertificateResponseDTO certificateResponseDTO) {
		Context context = new Context();
		Map<String, Object> data = new HashMap<>();
		data.put("certificate", certificateResponseDTO);
		data.put("download", true);
		try {
			data.put("qrImage", qrCodeGenerator.getQRCodeImage(certificateResponseDTO.getCertificateId(), 100, 100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.setVariables(data);
		return context;
	}
}