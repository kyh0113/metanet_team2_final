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
			log.info("[[AOP-around Log-exception]]]");
		}
		
		Long end = System.currentTimeMillis();
		
		log.info("[[[AOP-around execution time-{},methodName-{}]]",(end-start), methodName);
		return result;
	}	
	
	@AfterThrowing(pointcut="execution(* com.example.myapp..EmployeeController.*(..))", throwing="exception")
	public void EmployeeTHrowingLog(JoinPoint joinPoint, Exception exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		log.info("[[[AOP-after Log]]]-{}, ex: {}",methodName,exception.getMessage());
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
			log.info("[[AOP-around Log-exception]]]");
		}
		
		Long end = System.currentTimeMillis();
		
		log.info("[[[AOP-around execution time-{},methodName-{}]]",(end-start), methodName);
		return result;
	}	
	
	@AfterThrowing(pointcut="execution(* com.example.myapp..VacationController.*(..))", throwing="exception")
	public void VacationTHrowingLog(JoinPoint joinPoint, Exception exception) {
		Signature signature = joinPoint.getSignature();
		String methodName = signature.getName();
		log.info("[[[AOP-after Log]]]-{}, ex: {}",methodName,exception.getMessage());
	}	
		
	
}
