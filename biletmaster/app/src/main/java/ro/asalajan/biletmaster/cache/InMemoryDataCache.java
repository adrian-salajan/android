package ro.asalajan.biletmaster.cache;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDataCache implements DataCache {


    private Map<Integer, Object> objectsById;

    public InMemoryDataCache() {
        objectsById = new HashMap<>();
    }

    @Override
    public void put(int id, Object o) {
        objectsById.put(id, o);
    }

    @Override
    public Object get(int id) {
        Object cached = objectsById.get(id);
        if (cached == null) {
            return null;
        }
        return cached;
    }

    @Override
    public void clear() {
        objectsById.clear();
    }

    public ImmutableMap<Integer, Object> getContents() {
        return ImmutableMap.copyOf(objectsById);
    }
}
