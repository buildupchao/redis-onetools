package com.project.redis.onetools.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RateLimiterConfiguration {

	@Bean
	public PermitsTemplate permitsTemplate(RedisConnectionFactory connectionFactory) {
		PermitsTemplate template = new PermitsTemplate();
		template.setConnectionFactory(connectionFactory);
		return template;
	}
}
