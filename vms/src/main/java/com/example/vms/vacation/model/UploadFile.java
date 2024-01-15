package com.example.vms.vacation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UploadFile {
	private Integer fileId;
	private String name;
	private Integer fileSize;
	private String contentType;
	private byte[] fileData;
	private Integer regId; // 신청서 아이디
	
	public UploadFile() {
		
	}
	
	public UploadFile(String name, int fileSize, String contentType) {
		this.name = name;
		this.contentType = contentType;
		this.fileSize = fileSize;
	}

	public void setFileSize(Long fileSize) {
	    this.fileSize = fileSize != null ? fileSize.intValue() : null;
	}
}
