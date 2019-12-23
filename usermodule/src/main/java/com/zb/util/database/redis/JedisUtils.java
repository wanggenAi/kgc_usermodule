package com.zb.util.database.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

public class JedisUtils {
    // 声明连接池对象
    private static JedisPool pool;

    // 加载配置文件
    static {
        /**
         * ResourceBundle属于java.util.ResourceBundle包
         *      专门处理properties文件
         *      不用new，可直接使用--->ResourceBundle.getBundle("config/redis");
         *      getBundle(String baseName)方法中baseName就是配置文件名，我们这里是config
         *      getBundle(String baseName)方法获取的对象有getString(String key)方法
         *      getString(String key)方法可获取配置文件中的值，key就是配置文件key的名字
         *      因为获取的值都是String形式，所以要转换一下-->Integer
         */
        ResourceBundle config = ResourceBundle.getBundle("config/redis");
        String host = config.getString("redis.host"); // 远程ip地址
        int port = Integer.parseInt(config.getString("redis.port"));
        int maxIdle = Integer.parseInt(config.getString("redis.maxIdle"));
        int maxTotal = Integer.parseInt(config.getString("redis.maxTotal"));
        boolean testOnBorrow = Boolean.parseBoolean(config.getString("redis.testOnBorrow"));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        pool = new JedisPool(jedisPoolConfig, host, port);
    }

    /**
     * 从连接池获取一个Jedis实例
     * @return
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static void close(JedisPool pool) {
        if (pool != null) {
            pool.close();
        }
    }

    public static void main(String[] args) {

    }

    // 添加
    public static void set(String key, String value){
        Jedis jedis = getJedis();
        jedis.set(key, value);
        close(jedis);;
    }

    // 设置超时时间
    public static void expire(String key,int second) {
        Jedis jedis = getJedis();
        jedis.expire(key, second);
        close(jedis);
    }

    // 添加，带超时时间
    public static void setex(String key, int seconds, String value){
        Jedis jedis = getJedis();
        jedis.setex(key, seconds, value);
        close(jedis);;
    }

    // 获取
    public static String get(String key){
        Jedis jedis = getJedis();
        String value = jedis.get(key);
        close(jedis);
        return value;
    }

    // 查看某个键是否存在
    public static boolean exists(String key){
        Jedis jedis = getJedis();
        Boolean exists = jedis.exists(key);
        close(jedis);
        return exists;
    }

    // 查看超时时间
    public static Long ttl(String key){
        Jedis jedis = getJedis();
        Long ttl = jedis.ttl(key);
        close(jedis);
        return ttl;
    }

    // 删除
    public static void del(String key){
        Jedis jedis = getJedis();
        jedis.del(key);
        close(jedis);
    }

    // 给键自增1
    public static long incr(String key) {
        Jedis jedis = getJedis();
        long count = jedis.incr(key);
        close(jedis);
        return count;
    }


}
