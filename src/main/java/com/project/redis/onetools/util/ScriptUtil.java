package com.project.redis.onetools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptUtil {
	
	public static String getScript(String path) {
		InputStream inputStream = ScriptUtil.class.getClassLoader().getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer buffer = new StringBuffer();
		try {
			String str = "";
			while ((str = reader.readLine()) != null) {
				buffer.append(str).append(System.lineSeparator());
			}
		} catch (IOException ex) {
			log.error(">>>>Error Here!!!", ex.getMessage());
		}
		return buffer.toString();
	}
}
