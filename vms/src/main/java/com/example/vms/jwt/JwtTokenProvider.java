package com.example.vms.jwt;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.vms.employee.service.IEmployeeService;
import com.example.vms.manager.model.Employee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 어노테이션
public class JwtTokenProvider {
	private static final String SECRET_KEY="ASDFGHJKLJHGFDFGlkjhgfdsaasdfghjkHJKLKJHGFDFGHJKJHGFD";
	
	// private static String id = UUID.randomUUID().toString();
//	private static String id ="qwertyuiop"; // MalAlgorithm사용하면 이런 키 상관 없음
	private static MacAlgorithm alg = Jwts.SIG.HS256; // 복호키가 없어도 따로 상관 없음
	private static SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

	//	private static SecretKey key = alg.key().build();
	
	// 토큰 유효기간
	private long tokenValidTime = 30*60*1000L; // 30분
	
	@Autowired
	IEmployeeService employeeService;
	
	// 토큰 생성
	@Autowired
	private UserDetailsService userDetailsService;
	
	@PostConstruct
	public void init() {
		log.debug("token provider bean created");
	}
	
	public String generateToken(Employee employee) {
		// 사용자의 권한을 가져오기
		Set<String> roles = employeeService.getRolesByEmpId(employee.getEmpId());
		System.out.println("roles " + roles); // 이게 안나왔네
		System.out.println("employee.getEmpId() " + employee.getEmpId());
		System.out.println("employee.getName() " + employee.getName());
		System.out.println("employee.getDeptId() " + employee.getDeptId());
		if (employee == null || employee.getEmpId() == null
		        || employee.getName() == null
		        || roles == null || roles.isEmpty()
		        || employee.getDeptId() == 0) {
		    System.out.println("값이 비어있는거야야야야야야야야야야");
		    System.out.println("이게 출력되면 안돼");
		    log.error("Invalid employee information for token generation");
		    // 예외 처리 또는 기본값 설정
		    return null; // 또는 기본값 설정
		}
	    
	    System.out.println("employee 데이터가 다 있는거야");
	    
	    // 토큰이 가져야할 정보는? Header, Payload, Signature <- 이걸 이용해서 토큰을 만들어
	    long now = System.currentTimeMillis();
	    Claims claims = Jwts.claims()
	            .subject(employee.getEmpId())
	            .issuer(employee.getName())
	            .issuedAt(new Date(now))
	            .expiration(new Date(now + tokenValidTime))
	            .add("roles", roles)
	            .add("deptId", employee.getDeptId())
	            .build();
	    System.out.println("sdfsdfsdfsdfsdfsdf" + claims);
	    String token= Jwts.builder()
	            .claims(claims)
	            .signWith(key)
	            .compact();
	    System.out.println("token : " + token);
	    return token;
	}
	
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("X-AUTH-TOKEN");
	}
	
	// 토큰이 유효한지 확인
	public boolean validateToken(String token) {
		try { // 토큰을 분해하는 과정에서 예외 발생할 수 있음
			Jws<Claims> claims = Jwts.parser()
					.verifyWith(key).build()
					.parseSignedClaims(token);
			return !claims.getPayload().getExpiration().before(new Date());
		}catch(Exception e) {
			return false;
		}
	}
	
	public String getEmpId(String token) {
		log.info("JwtTokenProvider의 getId메서드임" + token); // 이게 null 이 출력됨
		return Jwts.parser().verifyWith(key).build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	// 사용자의 인증 정보를 가져오는 메서드
	public Authentication getAuthentication(String token) {
		System.out.println("getAuthentication메서드에 토큰을 받음" + token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmpId(token)); // 사용자의 식별자ID를 추출
		return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
	}
}