package com.project.redis.onetools.limiter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.redis.onetools.annotation.SyncLockable;

@Component
public class SyncLockHandler {

	@Autowired
	private SyncLockFactory syncLockFactory;
	
	@Around("")
	Object syncLock(ProceedingJoinPoint pjp, SyncLockable syncLockable) throws Throwable {
		SyncLock lock = syncLockFactory.build(syncLockable.key(), syncLockable.expire());

		try {
			lock.lock();
			return pjp.proceed();
		} finally {
			lock.unLock();
		}
	}
}
