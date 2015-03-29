package main.java.managers.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisManager<T> implements MemoryPoolManager<T>{

    Logger log = Logger.getAnonymousLogger();
    Set clusterNodes = new HashSet<>();

    private JedisPool pool;
    private JedisCluster cluster;
    private Jedis client;
    private String namespace;

    private Boolean inCluster = false;

    public RedisManager(JedisPool pool, String namespace) {
        this.namespace = namespace;
        this.pool = pool;

        if (!inCluster) {
            client = pool.getResource();
        } else {
            throw new RuntimeException("not accepted constructor in Jedis Cluster!");
        }
    }

    public RedisManager(String host, int port, String namespace) {

        this.namespace = namespace;
        if (inCluster) {
            clusterNodes.add(new HostAndPort(host, port));
            cluster = new JedisCluster(clusterNodes);
        } else {
            client = new Jedis(host, port);
        }

    }

    public JedisPool getPool() {
        return pool;
    }

    public Jedis getClient() {
        return client;
    }

    public void setClient(Jedis client) {
        this.client = client;
    }

    @Interceptors(RedisInterceptor.class)
    public T get(String key) {

        Jedis src = pool.getResource();
        String value = "";
        T result;
        try {
            if (inCluster) {
                value = cluster.get(namespace + ":" + key);
            } else {
                value = src.get(namespace + ":" + key);
            }

            result = (T) new MemoryObject<>(value).getObject();
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }
        return result;
    }

    @Interceptors(RedisInterceptor.class)
    public void set(T value) {
        Jedis src = pool.getResource();

        try {
            MemoryObject obj = new MemoryObject<>(value);
            String setVal = obj.toString();

            if (inCluster) {
                cluster.set(namespace + ":" + obj.getId(), setVal);
            } else {
                src.set(namespace + ":" + obj.getId(), setVal);
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }
    }

    @Interceptors(RedisInterceptor.class)
    public void setRawKey(String key, Object item) {
        Jedis src = pool.getResource();
        try {
            if (inCluster) {
                cluster.set(key, item.toString());
            } else {
                client.set(key, item.toString());
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }
    }

    @Interceptors(RedisInterceptor.class)
    public Object getRawKey(String key) {
        Jedis src = pool.getResource();
        Object res;
        try {
            if (inCluster) {
                res = cluster.get(key);
            } else {
                res = client.get(key);
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }
        return res;
    }

    @Interceptors(RedisInterceptor.class)
    public void pushDateList(String key, DateTime item) {
        Jedis src = pool.getResource();
        try {
            String dateString = item.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"));
            if (inCluster) {
                cluster.lpush(key, dateString);
            } else {
                client.lpush(key, dateString);
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }
    }

    @Interceptors(RedisInterceptor.class)
    public DateTime popDate(String key) {
        Jedis src = pool.getResource();
        DateTime res;
        try {
            if (inCluster) {
                res = DateTime.parse(cluster.rpop(key));
            } else {
                res = DateTime.parse(client.rpop(key));
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }

        return res;
    }

    @Interceptors(RedisInterceptor.class)
    public void addList(String key, List<T> list) {
        Jedis src = pool.getResource();

        try {
            if (inCluster) {
                cluster.del(key);
            } else {
                client.del(key);
            }

            for (T l : list) {
                try {
                    //FIXME - есть проблема конвертации в JSON внутри контейнера
                    String toPush = new MemoryObject<>(l).toString();
                    if (inCluster) {
                        cluster.lpush(key, toPush);
                    } else {
                        client.lpush(key, toPush);
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }

    }

    @Interceptors(RedisInterceptor.class)
    public List<T> getRange(String key, int start, int end) {
        List<String> lst;
        List<T> convertedList = new ArrayList<>();

        Jedis src = pool.getResource();

        try {
            if (inCluster) {
                lst = cluster.lrange(key, start, end);
            } else {
                lst = src.lrange(key, start, end);
            }

            for (String stringObj : lst) {
                T convertedObject = (T) new MemoryObject<T>(stringObj).getObject();
                convertedList.add(convertedObject);
            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }

        return convertedList;
    }

    @Interceptors(RedisInterceptor.class)
    public List<T> getRangeString(String key, int start, int end, T clonable) {

        List<String> lst;
        List<T> convertedList = new ArrayList<>();
        Jedis src = pool.getResource();

        try {
            if (inCluster) {
                lst = cluster.lrange(key, start, end);
            } else {
                lst = client.lrange(key, start, end);
            }

            for (String stringObj : lst) {
                try {
                    T clone = (T) clonable.getClass().getMethod("clone").invoke(clonable);
                    convertedList.add(clone);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    log.log(Level.SEVERE, e.getMessage());
                }

            }
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }

        return convertedList;
    }

    @Interceptors(RedisInterceptor.class)
    public List<T> trimList(String key, int start, int end) {

        List<T> res;
        Jedis src = pool.getResource();

        try {
            if (inCluster) {
                cluster.ltrim(key, start, end);
            } else {
                client.ltrim(key, start, end);
            }
            res = getRange(key, 0, -1);
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Error->" + e.toString());
            }
        }

        return res;
    }

    public void close() {
        if (inCluster) {
            cluster.close();
        } else {
            client.close();
        }
    }
}
