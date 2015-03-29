package main.java.managers;

import main.java.managers.interfaces.RedisPoolManager;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

@Singleton
public class JedisPoolProvider {

    private JedisPool pool;

    @PostConstruct
    public void init() {
        pool = new JedisPool("localhost", 6379);
    }

    @PreDestroy
    public void destroy() { pool.destroy();}

    public JedisPool getPool() {
        return pool;
    }
}
