package com.example.vms.vacation.service;

import com.example.vms.vacation.model.UploadFile;

public interface IUploadFileService {
	void insertUploadFile(UploadFile uploadFile);
	public int maxFileId();
}
