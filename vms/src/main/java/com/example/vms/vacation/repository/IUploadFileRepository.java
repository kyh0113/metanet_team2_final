package com.example.vms.vacation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.vms.vacation.model.UploadFile;

@Repository
@Mapper
public interface IUploadFileRepository {
	void insertUploadFile(UploadFile uploadFile);
	public int maxFileId();
	List<UploadFile> selectFileListByRegId(int regId);
	UploadFile selectFile(int fileId);
	
}
