package ro.asalajan.biletmaster.services;

public interface DataCache {

    void put(int id, Object o);
    Object get(int id);
    void clear();

}
