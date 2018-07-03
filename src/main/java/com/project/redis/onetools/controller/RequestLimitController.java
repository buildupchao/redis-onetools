package com.project.redis.onetools.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.redis.onetools.annotation.SpringLimit;

@RestController
@RequestMapping("/request/limit")
public class RequestLimitController {

	@SpringLimit(errorCode = 500000, errorMsg = "Please wait 1 minute.")
	@RequestMapping(value = "/p1", method = RequestMethod.GET)
	public Timestamp processs() {
		return Timestamp.from(Instant.now());
	}
	
	@RequestMapping(value = "/p2", method = RequestMethod.GET)
	public String processNoLimit() {
		return String.format("Hello %s.\n", new Random().nextInt(100));
	}
}
