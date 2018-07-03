package com.project.redis.onetools.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;

@RestController
@RequestMapping("/limit")
public class LimitController {

	final RateLimiter rateLimiter = RateLimiter.create(2.0);
	private LoadingCache<Long, AtomicLong> counter = CacheBuilder.newBuilder()
			.expireAfterWrite(2, TimeUnit.SECONDS)
			.build(new CacheLoader<Long, AtomicLong>() {
				@Override
				public AtomicLong load(Long key) throws Exception {
					return new AtomicLong(0);
				}
				
			});

	@RequestMapping("/p2")
	public void process2() {
		List<Runnable> tasks = new ArrayList<>();
		tasks.add(new Task());
		Executor executor = Executors.newFixedThreadPool(10);
		submitTasks(tasks, executor);
	}
	
	@RequestMapping(value = "/p3", produces = "text/plain;charset=UTF-8", method = RequestMethod.GET)
	public ResponseEntity<String> getData() throws ExecutionException {
		long currentSeconds = System.currentTimeMillis() / 1000;
		if (counter.get(currentSeconds).incrementAndGet() > 5L) {
			return new ResponseEntity<>("访问频率过快", HttpStatus.FORBIDDEN);
		}
		new Thread(new Task()).start();
		return null;
	}
	
	void submitTasks(List<Runnable> tasks, Executor executor) {
		for (Runnable task : tasks) {
			rateLimiter.acquire();
			executor.execute(task);
		}
	}

	class Task implements Runnable {
		@Override
		public void run() {
			for (int i = 1; i < 1000001; i++) {
				if (i % 1000000 == 0) {
					System.err.println(" thread → "
							+ Timestamp.from(Instant.now()));
				}
			}
		}
	}
}
