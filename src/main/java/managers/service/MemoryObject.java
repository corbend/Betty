package main.java.managers.service;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemoryObject<T> implements Iterator<T>{

    private Logger log = Logger.getAnonymousLogger();
    private String id;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String innerVal;

    private T object;
    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public MemoryObject(T value) {

        try {
            innerVal = rawConvert(value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map mp = JsonReader.jsonToMaps(innerVal);
        id = mp.get("id").toString();
    }

    public MemoryObject(String value) {

        T gObject = (T) JsonReader.jsonToJava(value);
        log.log(Level.INFO, "GET OBJECT=", gObject.toString());
        object = gObject;

    }

    private String rawConvert(Object item) throws UnsupportedEncodingException {
        Map<String, Object> optionalArgs = new HashMap<>();
        ByteArrayOutputStream e = new ByteArrayOutputStream();
        JsonWriter writer = new JsonWriter(e, optionalArgs);
        writer.write(item);
        writer.close();
        return new String(e.toByteArray(), "UTF-8");
    }

    private List<T> records = new ArrayList<>();

    public T next() {
        return records.iterator().next();
    }

    public void remove() {
        records.iterator().remove();
    }

    public boolean hasNext() {
        return records.iterator().hasNext();
    }


    @Override
    public String toString() {
        return innerVal;
    }
}
