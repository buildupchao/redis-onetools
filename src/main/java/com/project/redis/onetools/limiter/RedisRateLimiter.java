package com.project.redis.onetools.limiter;

import java.util.concurrent.TimeUnit;

import com.project.redis.onetools.configuration.PermitsTemplate;

public class RedisRateLimiter {

	private String key;
	private double permitsPerSecond;
	private int maxBurstSeconds = 60;
	private PermitsTemplate permitsTemplate;
	private SyncLock syncLock;

	public RedisRateLimiter(String key, double permitsPerSecond, int maxBurstSeconds, PermitsTemplate permitsTemplate, SyncLock syncLock) {
		super();
		this.key = key;
		this.permitsPerSecond = permitsPerSecond;
		this.maxBurstSeconds = maxBurstSeconds;
		this.permitsTemplate = permitsTemplate;
		this.syncLock = syncLock;
	}

	private RedisPermits putDefaultPermits() {
		RedisPermits redisPermits = new RedisPermits(permitsPerSecond, maxBurstSeconds);
		permitsTemplate.opsForValue().set(key, redisPermits, redisPermits.expires(), TimeUnit.SECONDS);
		return redisPermits;
	}

	public RedisPermits get() {
		RedisPermits redisPermits = permitsTemplate.opsForValue().get(key);
		if (redisPermits == null) {
			redisPermits = putDefaultPermits();
		}
		return redisPermits;
	}

	public void set(RedisPermits redisPermits) {
		permitsTemplate.opsForValue().set(key, redisPermits, redisPermits.expires(), TimeUnit.SECONDS);
	}

	public long now() {
		Long now = permitsTemplate.execute((connection) -> connection.time());
		if (now == null) {
			now = System.currentTimeMillis();
		}
		return now;
	}
	
	public long acquire(long tokens) {
		// to be done
		return -1;
	}
	
	public long acquire() {
		return acquire(1);
	}
	
	public boolean tryAcquire(long tokens, long timeout, TimeUnit unit) {
		
		return false;
	}
	
	public boolean tryAcquire(long timeout, TimeUnit unit) {
		return tryAcquire(1, timeout, unit);
	}
}
