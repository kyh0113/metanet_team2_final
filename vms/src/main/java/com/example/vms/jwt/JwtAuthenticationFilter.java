package com.example.vms.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
//
//@AllArgsConstructor
//public class JwtAuthenticationFilter extends GenericFilterBean {
//	/* 요청 객체로부터 전달된 토큰을 읽어야 함. 이유는 그 토큰이 유효한지 확인하려고.
//	 * 유효하면 그걸 가지고 authentication 객체를 만들어서
//	 * 그 객체를 스프링 컨텍스트에 넣는 작업이 있어야함
//	 * 토큰을 전송하는 방법이 여러개 있음. 근데 나는 헤더를 통해 보낼거임. */
//	
//	JwtTokenProvider jwtTokenProvider; // 이거를 의존 왜의존해? 토큰이 유효한지 확인하기 위해서 
//	
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		// 토큰을 읽기(요청헤더에서 읽을거임)
//		HttpServletRequest req = (HttpServletRequest) request;
//		String token = jwtTokenProvider.resolveToken(req);
//		
//		// 토큰이 유효한지 확인
//		if(token !=null && jwtTokenProvider.validateToken(token)) {
//			// 토큰에서 사용자 정보를 빼내서 Authentication 객체를 생성
//			Authentication auth = jwtTokenProvider.getAuthentication(token);
//			
//			// Authentication 객체를 SerucityContext에 set해야함
//			SecurityContext context = SecurityContextHolder.getContext();
//			context.setAuthentication(auth);
//		}
//		
//		chain.doFilter(request, response);
//

@AllArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 토큰을 읽기(요청 헤더 또는 쿠키에서 읽을 수 있음)
        HttpServletRequest req = (HttpServletRequest) request;
        String token = jwtTokenProvider.resolveToken(req);

        // 토큰이 유효한지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 사용자 정보를 빼내서 Authentication 객체를 생성
            Authentication auth = jwtTokenProvider.getAuthentication(token);

            // Authentication 객체를 SecurityContext에 set
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}



