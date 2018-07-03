package com.project.redis.onetools.limit;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalRateLimit  {
	
	static AtomicInteger counter = new AtomicInteger(0);
	static int limit = 200;
	static long timestamp = System.currentTimeMillis();
	
	private NormalRateLimit(NormalRateLimit.Builder builder) {
		limit = builder.limit;
		log.info("Created NormalRateLimit object[counter={}, limit={}]", counter.get(), limit);
	}

	public boolean limit() {
		long now = System.currentTimeMillis();
		if (now - timestamp < 1000) {
			if (counter.get() < limit) {
				counter.incrementAndGet();
				return true;
			} else {
				return false;
			}
		} else {
			counter.set(0);
			timestamp = now;
			return false;
		}
	}
	
	public static class Builder  {
		private int limit = 200;
		
		public Builder() {
			super();
		}

		public Builder limit(int limit) {
			this.limit = limit;
			return this;
		}
		
		public NormalRateLimit build() {
			return new NormalRateLimit(this);
		}
	}
}
