package main.java.managers.interfaces;

import redis.clients.jedis.JedisPool;

import javax.enterprise.inject.Default;


@Default
public interface RedisPoolManager {

    public JedisPool getPool();
}
