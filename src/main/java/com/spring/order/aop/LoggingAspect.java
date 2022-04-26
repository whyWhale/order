package com.spring.order.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	// @Around("com.spring.order.aop.CommonPointCut.servicePublicMethodPointCut()")
	// // order..${class}.${method}(${return type})
	// public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
	// 	long before = System.currentTimeMillis();
	// 	log.info("Before method called . {}", joinPoint.getSignature().toString());
	// 	Object proceed = joinPoint.proceed();
	// 	log.info("Ater method called . {}", proceed);
	// 	long after = System.currentTimeMillis();
	//
	// 	log.info("exec time : {}", after - before);
	// 	return proceed;
	// }

	@Around("@annotation(com.spring.order.aop.TrackTime)")
	public Object execTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.nanoTime();// 1초 -> 1000 밀리 세컨드 // 1밀리 세컨드 -> 1000마이크로 세컨트 / 1마이크로 세컨드 -> 1000나노 세컨드
		Object proceed = joinPoint.proceed();
		long end = System.nanoTime();// 1초 -> 1000 밀리 세컨드 // 1밀리 세컨드 -> 1000마이크로 세컨트 / 1마이크로 세컨드 -> 1000나노 세컨드

		log.info("exec time =>  {} ", end - start);
		return proceed;
	}

}
