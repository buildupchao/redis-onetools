package com.project.redis.onetools.interceptor;

import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.limit.RedisRateLimit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RedisAspect {

	@Autowired
	private RedisRateLimit redisRateLimit;
	
	@Pointcut("@annotation(com.project.redis.onetools.annotation.RequestLimit)")
	public void check() {};
	
	@Before("check()")
	public void before(JoinPoint joinPoint) { 
		Objects.requireNonNull(joinPoint, "redisRateLimit cannot be null.");
		
		boolean limit = redisRateLimit.limit();
		if (!limit) {
			log.warn(">>>>>>request limited");
			throw new RuntimeException(">>>>>>request limited");
		}
	}
}
