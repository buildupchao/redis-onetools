package com.project.redis.onetools.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.configuration.PermitsTemplate;

@Component
public class RedisRateLimiterFactory {

	@Autowired
	private PermitsTemplate permitsTemplate;

	@Autowired
	private SyncLockFactory syncLockFactory;
	
	private final  Map<String, RedisRateLimiter> limiterMap = new ConcurrentHashMap<>();
	
	public RedisRateLimiter build(String key, double permitsPerSecond, int maxBurstSeconds) {
		if (!limiterMap.containsKey(key)) {
			limiterMap.putIfAbsent(key, buildRedisRateLimiter(key, permitsPerSecond, maxBurstSeconds));
		}
		return limiterMap.get(key);
	}
	
	private RedisRateLimiter buildRedisRateLimiter(String key, double permitsPerSecond, int maxBurstSeconds) {
		return new RedisRateLimiter(key, permitsPerSecond, maxBurstSeconds, permitsTemplate, syncLockFactory.build(String.valueOf(key + ":lock")));
	}
}
