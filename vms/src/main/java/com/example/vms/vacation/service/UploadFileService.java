package com.example.vms.vacation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.repository.IUploadFileRepository;

@Service
public class UploadFileService implements IUploadFileService{

	@Autowired
	IUploadFileRepository uploadFileDao;
	
	@Override
	public void insertUploadFile(UploadFile uploadFile) {
		uploadFileDao.insertUploadFile(uploadFile);
		
	}

	@Override
	public int maxFileId() {
		return uploadFileDao.maxFileId();
	}

}
