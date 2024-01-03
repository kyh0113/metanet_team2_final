package com.example.vms.certificate.service;

import com.example.vms.certificate.model.CertificateRequestDTO;

public interface ICertificateService {

	public int createCertificate(CertificateRequestDTO certificateDTO);
}
