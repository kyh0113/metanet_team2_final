package com.example.vms.error.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.vms.certificate.controller.CertificateController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorController {

	@GetMapping("/access-denied")
    public String getMethodName() {
        return "error/access-denied";
    }
}
