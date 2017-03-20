package ro.asalajan.biletmaster.cache;

import com.google.common.collect.ImmutableMap;

/**
 * Cache that will be persisted after calling save().
 * Calling load() will load data into the the inner cache.
 */
public abstract class PersistableCache implements DataCache {

    protected DataCache innerCache;

//    protected PersistableCache(DataCache innerCache) {
//        this.innerCache = innerCache;
//    }

    public abstract void save();
    public abstract void load();

    @Override
    public void put(int id, Object o) {
        innerCache.put(id, o);
    }

    @Override
    public Object get(int id) {
        return innerCache.get(id);
    }

    @Override
    public void clear() {
        innerCache.clear();
    }

    @Override
    public ImmutableMap<Integer, Object> getContents() {
        return innerCache.getContents();
    }
}
