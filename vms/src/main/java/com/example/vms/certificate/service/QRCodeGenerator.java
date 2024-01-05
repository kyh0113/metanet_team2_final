package com.example.vms.certificate.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.IOException;
import com.itextpdf.io.source.ByteArrayOutputStream;

@Service
public class QRCodeGenerator {

	// QR코드 이미지 생성
	public String getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		try {
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		} catch (Exception e) {
			return null;
		}
		return Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
	}
}
