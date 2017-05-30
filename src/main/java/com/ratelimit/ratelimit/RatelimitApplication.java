package com.ratelimit.ratelimit;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;

import java.io.FileNotFoundException;
import java.io.FileReader;

@SpringBootApplication
public class RatelimitApplication extends WebMvcConfigurerAdapter{

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RateLimitInterceptor()).addPathPatterns("/**");
		//registry.addInterceptor(new Interceptor()).addPathPatterns("*");
	}

	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		try {
			Jedis jedis_conn = RedisManager.get_connection();
			File file = new ClassPathResource("static/rate_limit.json").getFile();
			Object obj = parser.parse(new FileReader(file));
			JSONObject jsonObject = (JSONObject) obj;

			String client_id = (String) jsonObject.get("client");
			String pattern = client_id + "_*";
			RedisManager.deleteKeys(pattern);

			jedis_conn.set("rate_limit", jsonObject.toJSONString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SpringApplication.run(RatelimitApplication.class, args);
	}
}
