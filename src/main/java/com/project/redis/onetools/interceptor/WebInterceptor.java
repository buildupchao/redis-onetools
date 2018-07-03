package com.project.redis.onetools.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.project.redis.onetools.annotation.SpringLimit;
import com.project.redis.onetools.limit.NormalRateLimit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebInterceptor implements WebMvcConfigurer {

	@Autowired
	private NormalRateLimit normalRateLimit;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LimitInterceptor());
	}
	
	private class LimitInterceptor extends HandlerInterceptorAdapter {
		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
				throws Exception {
			if (normalRateLimit == null) {
				throw new NullPointerException("redisRateLimit cannot be null.");
			}
			
			if (handler instanceof HandlerMethod) {
				HandlerMethod method = (HandlerMethod) handler;
				SpringLimit annotation = method.getMethodAnnotation(SpringLimit.class);
				if (annotation == null) {
					return true;
				}
				
				boolean limit = normalRateLimit.limit();
				if (!limit) {
					log.warn(">>>>>>request limited, {}", annotation.errorMsg());
					response.sendError(annotation.errorCode(), annotation.errorMsg());
					return false;
				}
			}
			return true;
		}
	}
}
