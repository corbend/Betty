package main.java.managers.service;


import java.util.List;

public interface MemoryPoolManager<T> {

    public T get(String key);

    public void set(T value);

    public void addList(String key, List<T> lst);
    public List<T> getRange(String key, int ind1, int ind2);
}
