package com.example.vms.vacation.model;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class FileDownload {

	private ResponseEntity<byte[]> file;
}
