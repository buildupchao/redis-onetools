package com.project.redis.onetools.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.constant.RedisOneToolsConstant;
import com.project.redis.onetools.limit.NormalRateLimit;
import com.project.redis.onetools.limit.RedisRateLimit;

@Component
public class LimitConfiguration {

	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;
	
	@Bean
	public NormalRateLimit normalLimit() {
		return new NormalRateLimit.Builder().limit(10).build();
	}
	
	@Bean
	public RedisRateLimit redisRateLimit() {
		return new RedisRateLimit.Builder(jedisConnectionFactory, RedisOneToolsConstant.SINGLE).limit(200).build();
	}
}
