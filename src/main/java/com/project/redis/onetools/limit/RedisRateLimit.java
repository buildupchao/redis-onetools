package com.project.redis.onetools.limit;

import java.io.IOException;
import java.util.Collections;

import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.project.redis.onetools.constant.RedisOneToolsConstant;
import com.project.redis.onetools.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

@Slf4j
public class RedisRateLimit {

	private JedisConnectionFactory jedisConnectionFactory;
	private int type;
	private int limit = 200;
	
	static final int FAIL = 0;
	
	String script;
	
	private RedisRateLimit(RedisRateLimit.Builder builder) {
		this.jedisConnectionFactory = builder.jedisConnectionFactory;
		this.type = builder.type;
		this.limit = builder.limit;
		
		init();
	}
	
	private void init() {
		buildScript();
	}

	public boolean limit() {
		Object connection = getConnection();
		Object result = limitRequest(connection);
		
		return ((Long) result == FAIL) ? false : true;
	}
	
	private Object limitRequest(Object connection) {
		Object result = null;
		String key = String.valueOf(System.currentTimeMillis() / 1000);
		if (connection instanceof Jedis) {
			result = ((Jedis) connection).eval(
							script, 
							Collections.singletonList(key), 
							Collections.singletonList(String.valueOf(limit))
					);
			((Jedis) connection).close();
		} else {
			result = ((JedisCluster) connection).eval(
							script,
							Collections.singletonList(key),
							Collections.singletonList(String.valueOf(limit))
					);
			try {
				((JedisCluster) connection).close();
			} catch (IOException ex) {
				log.error("IOException", ex);
			}
		}
		return result;
	}

	private Object getConnection() {
		Object connection = null;
		if (type == RedisOneToolsConstant.SINGLE) {
			RedisConnection redisConnection = jedisConnectionFactory.getConnection();
			connection = redisConnection.getNativeConnection();
		} else if (type == RedisOneToolsConstant.CLUSTER) {
			RedisClusterConnection clusterConnection = jedisConnectionFactory.getClusterConnection();
			connection = clusterConnection.getNativeConnection();
		}
		return connection;
	}
	
	private void buildScript() {
		this.script = ScriptUtil.getScript("limit.lua");
	}

	public static class Builder {

		private JedisConnectionFactory jedisConnectionFactory;
		private int type;
		private int limit = 200;
		
		public Builder(JedisConnectionFactory jedisConnectionFactory, int type) {
			super();
			this.jedisConnectionFactory = jedisConnectionFactory;
			this.type = type;
		}
		
		public Builder limit(int limit) {
			this.limit = limit;
			return this;
		}

		public RedisRateLimit build() {
			return new RedisRateLimit(this);
		}
		
	}
}
