package com.project.redis.onetools.configuration;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.redis.onetools.limiter.RedisPermits;

@Component
public class PermitsTemplate extends RedisTemplate<String, RedisPermits> {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	{
		setKeySerializer(new StringRedisSerializer());
		setValueSerializer(new RedisSerializer<RedisPermits>() {

			@Override
			public byte[] serialize(RedisPermits t) throws SerializationException {
				try {
					return objectMapper.writeValueAsBytes(t);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public RedisPermits deserialize(byte[] bytes) throws SerializationException {
				if (bytes != null) {
					try {
						return objectMapper.readValue(bytes, RedisPermits.class);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
			
		});
	}
}
