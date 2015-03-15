package main.java.managers.service;

import com.cedarsoftware.util.io.JsonWriter;
import main.java.models.sys.ScheduleParser;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisManager<T> implements MemoryPoolManager<T>{

    Logger log = Logger.getAnonymousLogger();
    Set clusterNodes = new HashSet<>();
    private JedisCluster cluster;
    private Jedis client;
    private String namespace;
    private Boolean inCluster = false;

    public RedisManager(String host, int port, String namespace) {

        this.namespace = namespace;
        if (inCluster) {
            clusterNodes.add(new HostAndPort(host, port));
            cluster = new JedisCluster(clusterNodes);
        } else {
            client = new Jedis(host, port);
        }

    }

    public T get(String key) {
        String value = "";

        if (inCluster) {
            value = cluster.get(namespace + key);
        } else {
            value = client.get(namespace + key);
        }
        return (T) (new MemoryObject((T) value).getObject());
    }

    public void set(T value) {
        MemoryObject obj = new MemoryObject(value);
        String setVal = obj.toString();

        if (inCluster) {
            cluster.set(namespace + ":" + obj.getId(), setVal);
        } else {
            client.set(namespace + ":" + obj.getId(), setVal);
        }
    }

    public void addList(String key, List<T> list) {

        if (inCluster) {
            cluster.del(key);
        } else {
            client.del(key);
        }

        for (T l: list) {
            log.log(Level.INFO, "Model fields=" + l.getClass().getDeclaredFields().toString());
            log.log(Level.INFO, "Model to Redis=" + l.toString());
            try {
                ScheduleParser.Proxy proxy = (ScheduleParser.Proxy) l.getClass().getMethod("createProxy").invoke(l);
                Long entityId = (Long) l.getClass().getMethod("getId").invoke(l);
                log.log(Level.INFO, "Model Proxy=" + proxy.toString());
                //FIXME - есть проблема конвертации в JSON внутри контейнера
                //пока кладем в редис idшники, хотя это лишено смысла
                //MemoryObject t = new MemoryObject<>(proxy);
                String toPush = new MemoryObject<>(l).toString();
                if (inCluster) {
                    cluster.lpush(key, toPush);
                } else {
                    client.lpush(key, toPush);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                log.log(Level.INFO, e.getMessage());
            }
        }
    }

    public List<T> getRange(String key, int start, int end) {
        List<String> lst = new ArrayList<>();
        if (inCluster) {
            lst = cluster.lrange(key, start, end);
        } else {
            lst = client.lrange(key, start, end);
        }
        List<T> convertedList = new ArrayList<>();

        for (String stringObj: lst) {
            T convertedObject = (T) new MemoryObject<T>(stringObj).getObject();
            convertedList.add(convertedObject);
        }

        return convertedList;
    }

    public List<T> getRangeString(String key, int start, int end, T clonable) {

        List<String> lst = new ArrayList<>();
        if (inCluster) {
            lst = cluster.lrange(key, start, end);
        } else {
            lst = client.lrange(key, start, end);
        }
        List<T> convertedList = new ArrayList<>();

        for (String stringObj: lst) {
            try {
                T clone = (T) clonable.getClass().getMethod("clone").invoke(clonable);
                convertedList.add(clone);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                log.log(Level.SEVERE, e.getMessage());
            }

        }

        return convertedList;
    }

    public List<T> trimList(String key, int start, int end) {

        if (inCluster) {
            cluster.ltrim(key, start, end);
        } else {
            client.ltrim(key, start, end);
        }

        return getRange(key, 0, -1);
    }
}
