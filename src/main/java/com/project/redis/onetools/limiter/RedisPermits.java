package com.project.redis.onetools.limiter;

import java.util.concurrent.TimeUnit;

public class RedisPermits {

	private long maxPermits;
	private long storedPermits;
	private long intervalMillis;
	private long nextFreeTicketMillis;

	public RedisPermits(double permitsPerSecond, int maxBurstSeconds) {
		this(permitsPerSecond, maxBurstSeconds, -1);
	}
	
	public RedisPermits(double permitsPerSecond, int maxBurstSeconds, long nextFreeTicketMillis) {
		initDefaultValue(permitsPerSecond, maxBurstSeconds, nextFreeTicketMillis);
	}

	public RedisPermits(long maxPermits, long storedPermits, long intervalMillis, long nextFreeTicketMillis) {
		super();
		this.maxPermits = maxPermits;
		this.storedPermits = storedPermits;
		this.intervalMillis = intervalMillis;
		this.nextFreeTicketMillis = nextFreeTicketMillis;
	}

	public long expires() {
		long now = System.currentTimeMillis();
		return 2 * TimeUnit.MINUTES.toSeconds(1) * TimeUnit.MILLISECONDS.toSeconds(Math.max(nextFreeTicketMillis, now) - now);
	}
	
	public boolean reSync(long now) {
		if (now > nextFreeTicketMillis) {
			storedPermits = Math.min(maxPermits, storedPermits + (now - nextFreeTicketMillis) / intervalMillis);
			nextFreeTicketMillis = now;
			return true;
		}
		return false;
	}
	
	private void initDefaultValue(double permitsPerSecond, int maxBurstSeconds, long nextFreeTicketMillis) {
		if (maxBurstSeconds <= 0) {
			maxBurstSeconds = 60;
		}
		if (nextFreeTicketMillis <= 0) {
			nextFreeTicketMillis = System.currentTimeMillis();
		}
		this.maxPermits = (long) (permitsPerSecond * maxBurstSeconds);
		this.storedPermits = (long) permitsPerSecond;
		this.intervalMillis = (long) (TimeUnit.SECONDS.toMillis(1) / permitsPerSecond);
		this.nextFreeTicketMillis = nextFreeTicketMillis;
	}
}
