package com.example.vms.certificate.controller;

import java.util.HashSet;
import java.util.Set;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.Contexts;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.example.vms.certificate.model.Certificate;
import com.example.vms.certificate.model.CertificateRequestDTO;
import com.example.vms.certificate.model.CertificateResponseDTO;
import com.example.vms.certificate.repository.ICertificateRepository;
import com.example.vms.certificate.service.DataMapper;
import com.example.vms.certificate.service.DocumentGenerator;
import com.example.vms.certificate.service.ICertificateService;
import com.example.vms.certificate.service.QRCodeGenerator;
import com.example.vms.employee.controller.EmployeeController;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/certificate")
public class CertificateController {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
	
	@Autowired
	ICertificateService certificateService;
	@Autowired
	IEmployeeService employeeService;
	
	@Autowired
	private SpringTemplateEngine springTemplateEngine;	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private DocumentGenerator documentGenerator;
	
	@Autowired
	private QRCodeGenerator grCodeGenerator;
	
	@Autowired
	private JwtTokenProvider tokenProvider;

    @GetMapping("")
    public String getMethodName() {
        return "certificate/home";
    }
    
    @PostMapping("/generate")
    @ResponseBody
    public String generateCerticate(
    	@RequestBody CertificateRequestDTO certificateRequestDTO
    ) {
    	return certificateService.createCertificate(certificateRequestDTO);
    }
  
    @GetMapping("/search") 
    @ResponseBody
    public CertificateResponseDTO[] searchCertificatesByEmpId(
    	@RequestParam(name = "emp_id", required=false, defaultValue = "") String emp_id,
    	@RequestParam(name = "certificate_id", required = false, defaultValue = "") String certificate_id
    ) {
    	CertificateResponseDTO[] certificates;
    	if (!certificate_id.equals("")) {
    		certificates = certificateService.searchCertificatesByCertificateId(certificate_id);
    	} else {
    		certificates = certificateService.searchCertificatesByEmpId(emp_id);
    	}
    	return certificates;
    }
    
    @GetMapping("/download/generate")
    @ResponseBody
    public String generatePDFCertificate(
    	@RequestParam("certificate_id") String certificate_id
    ) {
    	String finalHtml = null; 
    	CertificateResponseDTO certificate = certificateService.searchCertificatesByCertificateId(certificate_id)[0];
    	Context dataContext = dataMapper.setData(certificate);
    	String type = certificate.getType();
    	if (type.equals("재직증명서")) {
    		finalHtml = springTemplateEngine.process("/certificate/employmentcertificate", dataContext); 
    	} else if (type.equals("퇴직증명서")) {
    		finalHtml = springTemplateEngine.process("/certificate/retirementcertificate", dataContext); 
    	} else if (type.equals("경력증명서")) {
    		finalHtml = springTemplateEngine.process("/certificate/careercertificate", dataContext); 
    	}
    		
    	documentGenerator.htmlToPdf(finalHtml, certificate.getEmpName()+" "+certificate.getType());
    	return "Success";
    }
    
    // 증명서 발급 페이지 
    @GetMapping("/request")
    public String requestCertificatePage(
       HttpServletRequest request,
       Model model
    ) {
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) {
            String empId = tokenProvider.getEmpId(token);
            Employee employee = employeeService.selectEmployee(empId);
            model.addAttribute("employee", employee);
            return "certificate/request";
        } else {
        	return "redirect:/employee/login";
        }
        
    }
    
    // 증명서 조회 페이지 
    @GetMapping("/view")
    public String certificateView(
    	HttpServletRequest request,
    	Model model
    ) {
		// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) {
            // 토큰에서 empId 추출
            String empId = tokenProvider.getEmpId(token);
        	model.addAttribute("empId", empId);
        	model.addAttribute("certificates", certificateService.searchCertificatesByEmpId(empId));
        	return "certificate/view";
        } else {
        	return "redirect:/employee/login";
        }
    }
    
    // 증명서 미리보기 화면 
    @GetMapping("/request/view")
    public String getMethodName(
        @RequestParam(name = "emp_id", defaultValue = "", required = false) String emp_id,
        @RequestParam(name="type", defaultValue="", required = false) String type,
        Model model
    ) {
        Employee employee = employeeService.selectEmployee(emp_id);
        model.addAttribute("employee", employee);
        model.addAttribute("today", certificateService.currentTime());
        model.addAttribute("deptName", certificateService.searchDepartNameByDeptId(employee.getDeptId()));

    	if (type.equals("재직증명서")) {
    		return "/certificate/employmentcertificatePreView";
    	} else if (type.equals("퇴직증명서")) {
    		return "/certificate/retirementcertificatePreView";
    	} else if (type.equals("경력증명서")) {
    		return "/certificate/careercertificatePreView";
    	}
		return null;
    }
    
    
    // 증명서 발급화면 
    @GetMapping("/download/view")
    public String certificateDownloadView(
    	@RequestParam(name = "emp_id", defaultValue = "", required = false) String emp_id,
    	@RequestParam(name="type", defaultValue="", required = false) String type,
    	Model model
    ) {
    	CertificateResponseDTO[] certificates = certificateService.searchCertificatesByEmpId(emp_id);
    	if (certificates.length == 0) {
    		return "redirect:/certificate/view?emp_id="+emp_id;
    	}
    	CertificateResponseDTO certificate = new CertificateResponseDTO();
    	for (CertificateResponseDTO cert: certificates) {
    		if (cert.getType().equals(type)) {
    			certificate = cert;
    		}
    	}
    	if (certificate.getEmail().isEmpty()) {
    		return "redirect:/certificate/view?emp_id="+emp_id;
    	}
    	model.addAttribute("certificate", certificate);
    	String qrImage;
    	try {
    		qrImage = grCodeGenerator.getQRCodeImage(certificate.getCertificateId(), 100, 100);
        	model.addAttribute("qrImage", qrImage);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	if (type.equals("재직증명서")) {
    		return "/certificate/employmentcertificate";
    	} else if (type.equals("퇴직증명서")) {
    		return "/certificate/retirementcertificate";
    	} else if (type.equals("경력증명서")) {
    		return "/certificate/careercertificate";
    	}
		return null;
    }
   
}
