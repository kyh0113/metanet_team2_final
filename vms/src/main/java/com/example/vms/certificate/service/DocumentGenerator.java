package com.example.vms.certificate.service;

import java.io.FileOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

@Service
public class DocumentGenerator {
	public String htmlToPdf(String processedHtml) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		try {
			
			String FONT = "src/main/resources/static/malgun.ttf";
			
			PdfWriter pdfwriter = new PdfWriter(byteArrayOutputStream);
			FontProvider fontProvider = new DefaultFontProvider(false, false, false);
			FontProgram fontProgram = FontProgramFactory.createFont(FONT);
			fontProvider.addFont(fontProgram);
			
			ConverterProperties convertProperties = new ConverterProperties();
			convertProperties.setFontProvider(fontProvider);
			
			HtmlConverter.convertToPdf(processedHtml, pdfwriter, convertProperties);
			
			FileOutputStream fout = new FileOutputStream("/Users/KOSA/Desktop/test.pdf");
			
			byteArrayOutputStream.writeTo(fout);
			byteArrayOutputStream.close();
			
			byteArrayOutputStream.flush();
			fout.close();
			
			return null;
			
		} catch(Exception e) {
			return null;
		}
	} 
}
