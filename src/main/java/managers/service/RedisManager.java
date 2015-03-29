package main.java.managers.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
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
            log.log(Level.INFO, "REDIS MANAGER-init");
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
        T result = null;
        try {
            if (inCluster) {
                value = cluster.get(namespace + ":" + key);
            } else {
                value = src.get(namespace + ":" + key);
            }

            result = (T) new MemoryObject<>(value).getObject();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
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
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
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
                src.set(key, item.toString());
            }
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
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
        Object res = null;
        try {
            if (inCluster) {
                res = cluster.get(key);
            } else {
                res = src.get(key);
            }
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Critical Error->" + e.toString());
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
                src.lpush(key, dateString);
            }
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Critical Error->" + e.toString());
            }
        }
    }

    @Interceptors(RedisInterceptor.class)
    public DateTime popDate(String key) {
        Jedis src = pool.getResource();
        DateTime res = null;
        try {
            if (inCluster) {
                res = DateTime.parse(cluster.rpop(key));
            } else {
                res = DateTime.parse(src.rpop(key));
            }
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Critical Error->" + e.toString());
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
                src.del(key);
            }

            for (T l : list) {
                try {
                    //FIXME - есть проблема конвертации в JSON внутри контейнера
                    String toPush = new MemoryObject<>(l).toString();
                    if (inCluster) {
                        cluster.lpush(key, toPush);
                    } else {
                        src.lpush(key, toPush);
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
            }
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
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
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
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
                lst = src.lrange(key, start, end);
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

        List<T> res = null;
        Jedis src = pool.getResource();

        try {
            if (inCluster) {
                cluster.ltrim(key, start, end);
            } else {
                src.ltrim(key, start, end);
            }
            res = getRange(key, 0, -1);
        } catch (JedisException e) {
            log.log(Level.SEVERE, e.toString());
        } finally {
            try {
                pool.returnResource(src);
            } catch (JedisException e) {
                log.log(Level.SEVERE, "Jedis Critical Error->" + e.toString());
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
