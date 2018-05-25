package com.project.redis.onetools.limiter;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;

public class SyncLock {

	private String key;
	private StringRedisTemplate stringRedisTemplate;
	private long expire;
	private long safetyTime;

	private long waitMillisPer = 10;

	public SyncLock(String key, StringRedisTemplate stringRedisTemplate, long expire, long safetyTime) {
		super();
		this.key = key;
		this.stringRedisTemplate = stringRedisTemplate;
		this.expire = expire;
		this.safetyTime = safetyTime;
	}

	private String value() {
		Thread thread = Thread.currentThread();
		return String.valueOf(thread.getId() + "-" + thread.getName());
	}

	public boolean tryLock() {
		boolean locked = Optional.ofNullable(stringRedisTemplate.opsForValue().setIfAbsent(key, value())).orElse(false);
		if (locked) {
			stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
		}
		return locked;
	}

	public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
		long waitMax = unit.toMillis(timeout);
		long waitAlready = 0;

		while (stringRedisTemplate.opsForValue().setIfAbsent(key, value()) != true && waitAlready < waitMax) {
			Thread.sleep(waitMillisPer);
			waitAlready += waitMillisPer;
		}

		if (waitAlready < waitMax) {
			stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
			return true;
		}
		return false;
	}

	public void lock() {
		long waitMax = TimeUnit.SECONDS.toMillis(safetyTime);
		long waitAlready = 0;

		try {
			while (stringRedisTemplate.opsForValue().setIfAbsent(key, value()) != true && waitAlready < waitMax) {
				Thread.sleep(waitMillisPer);
				waitAlready += waitMillisPer;
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		
		stringRedisTemplate.opsForValue().set(key, value(), expire, TimeUnit.SECONDS);
	}
	
	public void unLock() {
		String result = stringRedisTemplate.opsForValue().get(key);
		if (Objects.equals(result, value())) {
			stringRedisTemplate.delete(key);
		}
	}
}
