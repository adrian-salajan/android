package ro.asalajan.biletmaster.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import ro.asalajan.biletmaster.cache.DataCache;
import ro.asalajan.biletmaster.cache.FilePersistableCache;
import ro.asalajan.biletmaster.cache.InMemoryDataCache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestFilePersistableCache {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void saveAndLoadFromFile() throws IOException {
        File dir = folder.newFolder("cacheDir");
        InMemoryDataCache innerCache = new InMemoryDataCache();
        TestCache cache = new TestCache(innerCache, dir);

        Integer data = new Integer(5);
        cache.put(data.hashCode(), data);
        assertEquals(data, cache.get(data.hashCode()));

        cache.save();
        innerCache.clear();

        assertNull(cache.get(data.hashCode()));
        cache.load();
        assertEquals(data, cache.get(data.hashCode()));

    }

    class TestCache extends FilePersistableCache<Integer> {

        public TestCache(DataCache innerCache, File externalCacheDir) {
            super(innerCache, externalCacheDir);
        }

        @Override
        protected Gson getGson() {
            return new Gson();
        }

        @Override
        protected Type getType() {
            return new TypeToken<SerializedLine<Integer>>() {}.getType();
        }
    }
}
