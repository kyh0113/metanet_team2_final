package com.example.vms.vacation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UploadFile {
	Integer fileId;
	String name;
	Integer fileSize;
	String contentType;
	byte[] fileData;
	Integer regId; // 신청서 아이디
	
	public void setFileSize(Long fileSize) {
	    this.fileSize = fileSize != null ? fileSize.intValue() : null;
	}
}
