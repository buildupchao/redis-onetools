package com.project.redis.onetools.lock;

import java.util.Collections;

import redis.clients.jedis.Jedis;

public class RedisLock {

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_ABSENT = "NX";
	private static final String SET_WITH_EXPIRE = "PX";
	
	private static final Long RELEASE_SUCCESS = 1L;
	
	public static boolean tryDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
		String result = jedis.set(lockKey, requestId, SET_IF_ABSENT, SET_WITH_EXPIRE, expireTime);
		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
	
	public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
		String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(luaScript, Collections.singletonList(lockKey), Collections.singletonList(requestId));
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;
	}
	
}
