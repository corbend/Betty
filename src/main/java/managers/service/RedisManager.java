package main.java.managers.service;

import com.cedarsoftware.util.io.JsonWriter;
import main.java.models.sys.ScheduleParser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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
            value = cluster.get(namespace + ":" + key);
        } else {
            value = client.get(namespace + ":" + key);
        }

        T result = (T) new MemoryObject<>(value).getObject();
        return result;
    }

    public void set(T value) {
        MemoryObject obj = new MemoryObject<>(value);
        String setVal = obj.toString();

        if (inCluster) {
            cluster.set(namespace + ":" + obj.getId(), setVal);
        } else {
            client.set(namespace + ":" + obj.getId(), setVal);
        }
    }

    public void setRawKey(String key, Object item) {
        if (inCluster) {
            cluster.set(key, item.toString());
        } else {
            client.set(key, item.toString());
        }
    }

    public Object getRawKey(String key) {
        if (inCluster) {
            return cluster.get(key);
        } else {
            return client.get(key);
        }
    }

    public void pushDateList(String key, DateTime item) {
        String dateString = item.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"));
        if (inCluster) {
            cluster.lpush(key, dateString);
        } else {
            client.lpush(key, dateString);
        }
    }

    public DateTime popDate(String key) {
        if (inCluster) {
            return DateTime.parse(cluster.rpop(key));
        } else {
            return DateTime.parse(client.rpop(key));
        }
    }

    public void addList(String key, List<T> list) {

        if (inCluster) {
            cluster.del(key);
        } else {
            client.del(key);
        }

        for (T l: list) {
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

    public void close() {
        if (inCluster) {
            cluster.close();
        } else {
            client.close();
        }
    }
}
