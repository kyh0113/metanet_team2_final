package com.example.vms.vacation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.service.VacationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/vacation")
public class VacationController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private VacationService vacationService;

    @GetMapping("/request")
    public String requestVacation() {
        return "vacation/request";
    }

    @PostMapping("/request")
    public String requestVacation(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Vacation vacation) {
        // 토큰 추출
//        String token = tokenProvider.resolveToken(request);
//        System.out.println("쿠키로 토큰 가져옴 "+token); // 쿠키로 토큰 가져옴
    	
    		
    	
    	// 쿠키 정보
        Cookie[] cookies = request.getCookies();
        //System.out.println(cookies.toString());
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }

        // 토큰 유효성 검사
        if (tokenProvider.validateToken(token)) {
        	System.out.println("유효성 검사 들어옴");
            // 토큰에서 empId 추출
            String empId = tokenProvider.getEmpId(token);

            // 휴가 정보 설정
            vacation.setEmpId(empId);

            // 휴가 등록
            vacationService.requestVacation(vacation);

            // 성공적으로 등록되었으면 리스트 페이지로 리다이렉트

            // 쿠키에 토큰을 설정 (클라이언트 사이드에서 사용)
            response.setHeader("Set-Cookie", "X-AUTH-TOKEN=" + token + "; Path=/; HttpOnly");

            return "redirect:/vacation/list";
        } else {
            // 토큰이 유효하지 않으면 로그인 페이지로 리다이렉트 또는 에러 처리
            return "redirect:/employee/login"; // 또는 다른 처리 방식을 선택하세요.
        }
    }
}
