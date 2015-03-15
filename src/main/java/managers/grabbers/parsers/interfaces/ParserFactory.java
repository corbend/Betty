package main.java.managers.grabbers.parsers.interfaces;

public interface ParserFactory<T, V> {

    public T create(V persistentParser) throws InstantiationException, IllegalAccessException;
}
