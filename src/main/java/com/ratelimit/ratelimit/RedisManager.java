package com.ratelimit.ratelimit;

import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

/**
 * Created by sarang on 29/05/17.
 */
public class RedisManager {

    private static final String DELETE_SCRIPT_IN_LUA = "local keys = redis.call('keys', '%s')" +
            "  for i,k in ipairs(keys) do" +
            "    local res = redis.call('del', k)" +
            "  end";

    public static Jedis get_connection(){
        try{
            Jedis jedis_conn = new Jedis("localhost", 6379);
            if (jedis_conn == null) {
                throw new Exception("Unable to get jedis resource!");
            }
            return jedis_conn;
        } catch (Exception exc) {
            throw new RuntimeException("Unable to delete that pattern!");
        }
    }

    public static String get_key(String time_type, String client_id, String type){
        return client_id+"_"+type+"_"+time_type;
    }

    public static Integer get_key_expiry(String time_type){

        if (time_type.equals("SEC")){
            return 1;
        }else if (time_type.equals("MIN")){
            return 60;
        }else if (time_type.equals("HOUR")){
            return 60*60;
        }else if (time_type.equals("WEEK")){
            return 7*24*3600;
        }else if (time_type.equals("MONTH")){
            return 30*24*3600;
        }

        return 0;
    }

    public static boolean check_rate_limit(JSONObject limits, String client_id,
                                           String ratelimit_name, String time_type,
                                           Jedis jedis_conn){

        //System.out.println(limits);
        //Long default_val = new Long(0);
        Long time_type_limit = (Long) limits.getOrDefault(time_type, new Long(0));
        if (time_type_limit != 0){
            //System.out.println(time_type_limit);

            String time_key = RedisManager.get_key(time_type, client_id, ratelimit_name);
            Integer key_expiry = RedisManager.get_key_expiry(time_type);

            //Jedis jedis_conn = RedisManager.get_connection();

            String time_val = jedis_conn.get(time_key);

            System.out.println(time_key + " " + key_expiry + " " + time_val);
            if (time_val == null){
                time_type_limit -= 1;
                jedis_conn.setex(time_key, key_expiry, time_type_limit.toString());
            }else if (time_val.equals("0")) {
                return false;
            }else{
                jedis_conn.decr(time_key);
            }
        }
        return true;
    }


    public static void deleteKeys(String pattern) {
        Jedis jedis_conn = null;

        try {
            jedis_conn = RedisManager.get_connection();

            if (jedis_conn == null) {
                throw new Exception("Unable to get jedis resource!");
            }
            jedis_conn.eval(String.format(DELETE_SCRIPT_IN_LUA, pattern));
        } catch (Exception exc) {
            throw new RuntimeException("Unable to delete that pattern!");
        } finally {
            if (jedis_conn != null) {
                jedis_conn.close();
            }
        }
    }
}
