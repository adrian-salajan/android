package ro.asalajan.biletmaster.cache;

import com.google.common.collect.ImmutableMap;

public interface DataCache {

    void put(int id, Object o);
    Object get(int id);

    ImmutableMap<Integer, Object> getContents();
    void clear();

}
