package ro.asalajan.biletmaster.services.cache;

import com.google.common.collect.ImmutableMap;

/**
 * Cache that will be persisted after calling save().
 * Calling load() will overwrite the inner cache.
 */
public abstract class PersistableDataCache implements DataCache {

    protected DataCache innerCache;

    public PersistableDataCache() {
        innerCache = new InMemoryDataCache();
    }

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
