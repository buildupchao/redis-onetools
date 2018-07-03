package com.project.redis.onetools.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.limit.NormalRateLimit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class NormalAspect {

	@Autowired
	private NormalRateLimit normalRateLimit;
	
	@Pointcut("@annotation(com.project.redis.onetools.annotation.NormalLimit)")
	private void check() {};
	
	@Before("check()")
	public void before(JoinPoint joinPoint) {
		if (normalRateLimit == null)
			throw new NullPointerException("normalRateLimit is null, Please check!");
		
		boolean limit = normalRateLimit.limit();
		if (!limit) {
			log.warn(">>>>>>{} request limited", joinPoint.getSignature().getName());
			throw new RuntimeException(">>>>>>request limited");
		}
	}
}
