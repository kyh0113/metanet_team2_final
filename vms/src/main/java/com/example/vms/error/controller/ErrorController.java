package com.example.vms.error.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorController {

	@GetMapping("/access-denied")
	public String accessDenied(Model model, Authentication auth, HttpServletRequest req, @RequestParam(value = "errorMessage", required = false) String errorMessage) {
     
        if (errorMessage != null) {
            System.out.println("Error Message: " + errorMessage);
            if(errorMessage.contains("/leader")) {
            	model.addAttribute("errorMessage", "팀장");
            }
            else if(errorMessage.contains("/manager")) {
            	model.addAttribute("errorMessage", "관리자");
            } 
            else {
            	model.addAttribute("errorMessage", "팀원");
            }
        }

        return "error/access-denied";
    }
	
	@GetMapping("/login-denied")
	public String loginDenied(Model model, Authentication auth, HttpServletRequest req, @RequestParam(value = "errorMessage", required = false) String errorMessage) {
		return "/error/login-denied";
	}
}
