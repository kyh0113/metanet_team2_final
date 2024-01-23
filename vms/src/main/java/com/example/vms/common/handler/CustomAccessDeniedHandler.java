package com.example.vms.common.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		// 현재 요청된 URL을 가져와서 처리
        String requestedUrl = request.getRequestURI();
        
        String redirectUrl = "/error/access-denied?errorMessage=" + requestedUrl;
        // Access Denied 페이지로 리다이렉션
        response.sendRedirect(redirectUrl);
	}
}
