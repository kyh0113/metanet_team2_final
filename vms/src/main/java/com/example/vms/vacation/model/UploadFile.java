package com.example.vms.vacation.model;

import lombok.Data;

@Data
public class UploadFile {
	private int fileId;
	private String name;
	private long fileSize;
	private String contentType;
	private byte[] fileData;	
	private int regId;
}
