package com.project.redis.onetools.limiter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.annotation.SyncLockable;

@Component
public class SyncLockFactory {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static final long DEFAULT_EXPIRE = 10;
	private static final long DEFAULT_SAFETY_TIME = DEFAULT_EXPIRE * 5;
	
	private final Map<String, SyncLock> syncLockMap = new HashMap<>();
	
	@SyncLockable
	public SyncLock build(String key) {
		return build(key, DEFAULT_EXPIRE);
	}
	
	@SyncLockable
	public SyncLock build(String key, long expire) {
		return build(key, expire, DEFAULT_SAFETY_TIME);
	}
	
	@SyncLockable
	public SyncLock build(String key, long expire, long safetyTime) {
		if (!syncLockMap.containsKey(key)) {
			syncLockMap.put(key, getOrDefaultSyncLock(key, expire, safetyTime));
		}
		return syncLockMap.get(key);
	}
	
	private SyncLock getOrDefaultSyncLock(String key, long expire, long safetyTime) {
		if (expire <= 0) {
			expire = DEFAULT_EXPIRE;
		}
		if (safetyTime <= 0) {
			safetyTime = DEFAULT_SAFETY_TIME;
		}
		return new SyncLock(key, stringRedisTemplate, expire, safetyTime);
	}
}
