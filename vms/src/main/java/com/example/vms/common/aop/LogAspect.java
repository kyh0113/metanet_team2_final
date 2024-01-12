package com.example.vms.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAspect {

	@Around("execution(* com.example.vms..EmployeeService.*(..))")
	public Object aroundEmployeeLog(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		Long start = System.currentTimeMillis();
		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Throwable e) {
			log.info("[[AOP-around 에러발생]]]");
		}
		
		Long end = System.currentTimeMillis();
		
		log.info("[[[AOP-around 실행시간-{}milisec,실행메소드-{}]]",(end-start), methodName);
		return result;
	}	
	
	@AfterThrowing(pointcut="execution(* com.example.myapp..EmployeeController.*(..))", throwing="exception")
	public void EmployeeTHrowingLog(JoinPoint joinPoint, Exception exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		log.info("[[[AOP-Throwing Log]]]-{}, ex: {}",methodName,exception.getMessage());
	}
	
	@Around("execution(* com.example.vms..VacationService.*(..))")
	public Object aroundVacationLog(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		Long start = System.currentTimeMillis();
		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Throwable e) {
			log.info("[[AOP-around Log-에러발생]]]");
		}
		
		Long end = System.currentTimeMillis();
		
		log.info("[[[AOP-around 실행시간-{}milisec,실행메소드-{}]]",(end-start), methodName);
		return result;
	}	
	
	@AfterThrowing(pointcut="execution(* com.example.myapp..VacationController.*(..))", throwing="exception")
	public void VacationTHrowingLog(JoinPoint joinPoint, Exception exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		log.info("[[[AOP-Throwing Log]]]-{}, 예외: {}",methodName,exception.getMessage());
	}	
	@Around("execution(* com.example.vms..CertificateService.*(..))")
	public Object CertificateVacationLog(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		Long start = System.currentTimeMillis();
		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Throwable e) {
			log.info("[[AOP-around Log-에럽라생]]]");
		}
		
		Long end = System.currentTimeMillis();
		
		log.info("[[[AOP-around 실행시간-{}milisec,실행메소드-{}]]",(end-start), methodName);
		return result;
	}	
	
	@AfterThrowing(pointcut="execution(* com.example.myapp..CertificateController.*(..))", throwing="exception")
	public void CertificateTHrowingLog(JoinPoint joinPoint, Exception exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		log.info("[[[AOP-Throwing Log]]]-{}, 예외: {}",methodName,exception.getMessage());
	}	
		
	
}
