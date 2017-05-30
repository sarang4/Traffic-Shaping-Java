package com.ratelimit.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

/**
 * Created by sarang on 29/05/17.
 */

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String request_uri = request.getRequestURI();
        String request_method = request.getMethod();
        System.out.println(request_uri+" "+request_method);
        System.out.println("this is interceptor, preHandle method");

        JSONParser parser = new JSONParser();
        Jedis jedis_conn = RedisManager.get_connection();
        JSONObject val = (JSONObject) parser.parse(jedis_conn.get("rate_limit"));

        String client_id = (String) val.get("client");

        JSONArray specialization = (JSONArray) val.get("specialization");
        Iterator<JSONObject> iterator = specialization.iterator();

        while (iterator.hasNext()) {
            JSONObject rate_limit = iterator.next();

            String ratelimit_type = (String) rate_limit.get("type");
            String ratelimit_name = (String) rate_limit.get("name");

            //System.out.println(ratelimit_type + " " + ratelimit_name);
            if (ratelimit_type.equals("METHOD") && ratelimit_name.equals(request_method)){
                JSONObject limits = (JSONObject) rate_limit.get("limit");
                if (! (RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "SEC", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "MIN", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "HOUR", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "WEEK", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "MONTH", jedis_conn))){
                    System.out.println("Rate Limit Exceeded");
                    response.getWriter().write("Rate limit exceeded, wait for sometime");
                    response.setStatus(429);
                    
                    jedis_conn.close();
                    return false;
                }
            }else if (ratelimit_type.equals("API") && ratelimit_name.equals(request_uri)){
                JSONObject limits = (JSONObject) rate_limit.get("limit");
                if (! (RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "SEC", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "MIN", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "HOUR", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "WEEK", jedis_conn) &&
                        RedisManager.check_rate_limit(limits, client_id, ratelimit_name, "MONTH", jedis_conn))){
                    System.out.println("Rate Limit Exceeded");
                    response.getWriter().write("Rate limit exceeded, wait for sometime");
                    response.setStatus(429);

                    jedis_conn.close();
                    return false;
                }
            }
        }
        jedis_conn.close();
        return true;
    }

    public static void response_writer(HttpServletResponse response){
        response.addIntHeader("Rate limit exceeded, wait for sometime", 10);
        response.setStatus(429);
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        System.out.println("this is interceptor, postHandle method");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("this is interceptor, afterCompletion method");
    }
}
