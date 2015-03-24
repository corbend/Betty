package main.java.managers;

import main.java.managers.interfaces.RedisPoolManager;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class JedisPoolProvider {

    private JedisPool pool;

    @PostConstruct
    public void init() {
        pool = new JedisPool("localhost", 6379);
    }

    public JedisPool getPool() {
        return pool;
    }
}
