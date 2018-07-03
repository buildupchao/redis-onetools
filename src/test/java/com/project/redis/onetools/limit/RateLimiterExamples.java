package com.project.redis.onetools.limit;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterExamples {
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	public static void exec() {
		if (count.get() >= 5) {
			System.err.println("<<<<<<Request limited.");
		} else {
			count.incrementAndGet();
			try {
				TimeUnit.MILLISECONDS.sleep(10);
				System.out.println(">>>>>>" + (System.currentTimeMillis() / 1000));
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} finally {
				count.decrementAndGet();
			}
		}
	}
	
	public static void main(String[] args) {
		ExecutorService executors = Executors.newFixedThreadPool(10);
		CyclicBarrier barrier = new CyclicBarrier(10);
		for (int i = 0; i < 30; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			executors.submit(() -> {
				try {
					barrier.await();
					exec();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		executors.shutdown();
	}
}
