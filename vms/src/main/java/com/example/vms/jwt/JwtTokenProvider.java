package com.example.vms.jwt;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.apache.ibatis.parsing.TokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.manager.model.Employee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenProvider {

	private static final String SECRET_KEY="ASDFGHJKLJHGFDFGlkjhgfdsaasdfghjkHJKLKJHGFDFGHJKJHGFD";
	private static MacAlgorithm alg = Jwts.SIG.HS256; // 복호키가 없어도 따로 상관 없음
	private static SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    private long tokenValidTime = 30 * 60 * 1000L;

    @Autowired
    private IEmployeeService employeeService;
   

    @Autowired
    private UserDetailsService userDetailsService;

    public String generateToken(Employee employee) {
        Set<String> roles = employeeService.getRolesByEmpId(employee.getEmpId());
        if (employee == null || employee.getEmpId() == null
                || employee.getName() == null
                || roles == null || roles.isEmpty()
                || employee.getDeptId() == 0) {
            log.error("Invalid employee information for token generation");
            return null;
        }

        long now = System.currentTimeMillis();
        Claims claims = Jwts.claims()
                .subject(employee.getEmpId())
                .issuer(employee.getName())
                .issuedAt(new Date(now))
                .expiration(new Date(now + tokenValidTime))
                .add("roles", roles)
                .add("deptId", employee.getDeptId())
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

//    public String resolveToken(HttpServletRequest request) {
//        return request.getHeader("X-AUTH-TOKEN");
//    }
    
    public String resolveToken(HttpServletRequest request) {
        // 헤더에서 토큰을 읽어오는 기존 방식
        // return request.getHeader("X-AUTH-TOKEN");

        // 쿠키에서 토큰을 읽어오는 방식으로 변경
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("X-AUTH-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }

    public String getEmpId(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmpId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}


    
//    public boolean validateToken(String token) {
//        try {
//        	Jws<Claims> claims = Jwts.parser()
//        			.verifyWith(key).build()
//        			.parseSignedClaims(token);
//            return true;
//        } catch (Exception e) {
//            log.error("Token validation failed", e);
//            return false;
//        }
//    }
//
//    public String getEmpId(String token) {
//        return Jwts.parser().verifyWith(key).build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }
//
//    public Authentication getAuthentication(String token) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmpId(token));
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }
//}
