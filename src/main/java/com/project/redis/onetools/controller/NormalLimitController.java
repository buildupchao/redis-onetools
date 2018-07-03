package com.project.redis.onetools.controller;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.redis.onetools.annotation.NormalLimit;

@RestController
@RequestMapping("/normal/limit")
public class NormalLimitController {

	@NormalLimit
	@RequestMapping(value = "/p1", method = RequestMethod.GET)
	public String limit() {
		System.out.println(Timestamp.from(Instant.now()));
		return null;
	}
}
