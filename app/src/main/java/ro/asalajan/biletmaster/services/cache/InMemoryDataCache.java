package ro.asalajan.biletmaster.services.cache;

import com.google.common.collect.ImmutableMap;

import org.joda.time.Hours;
import org.joda.time.Instant;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDataCache implements DataCache {

   // private static final int CACHE_PERIOD_HOURS = 12;

    private Map<Integer, Object> objectsById;
    private Map<Integer, Instant> timestampById;

    public InMemoryDataCache() {
        objectsById = new HashMap<>();
       // timestampById = new HashMap<>();
    }

    @Override
    public void put(int id, Object o) {
        objectsById.put(id, o);
       // timestampById.put(id, Instant.now());
    }

    @Override
    public Object get(int id) {
        Object cached = objectsById.get(id);
        if (cached == null) {
            return null;
        }
//        if (isDataOld(id)) {
//            objectsById.remove(id);
//            return null;
//        }
        return cached;
    }

//    private boolean isDataOld(int id) {
//        Instant now = Instant.now();
//        Instant then = timestampById.get(id);
//        return Hours.hoursBetween(then, now).isGreaterThan(Hours.hours(CACHE_PERIOD_HOURS));
//    }

    @Override
    public void clear() {
        objectsById.clear();
        timestampById.clear();
    }

    public ImmutableMap<Integer, Object> getContents() {
        return ImmutableMap.copyOf(objectsById);
    }
}
