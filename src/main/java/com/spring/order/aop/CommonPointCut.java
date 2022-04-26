package com.spring.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointCut {

	@Pointcut("execution(public * com.spring.order..*.*(..))")
	public void servicePublicMethodPointCut() {

	}

	@Pointcut("execution(public * com.spring.order..*repository.*(..))")
	public void RepositoryPublicMethodPointCut() {

	}

	@Pointcut("execution(public * com.spring.order..*repository.insert(..))")
	public void RepositoryPublicInsertMethodPointCut() {

	}

}
