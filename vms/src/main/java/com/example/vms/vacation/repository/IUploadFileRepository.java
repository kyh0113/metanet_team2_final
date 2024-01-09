package com.example.vms.vacation.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.UploadFile;

@Repository
@Mapper
public interface IUploadFileRepository {
	void insertUploadFile(UploadFile uploadFile);
	public int maxFileId();
}
