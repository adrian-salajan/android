package ro.asalajan.biletmaster.cache;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.cache.FilePersistableCache;
import ro.asalajan.biletmaster.cache.InMemoryDataCache;

public class EventListCache extends FilePersistableCache<List<Event>> {

    public EventListCache(File externalCacheDir) {
        super(new InMemoryDataCache(), externalCacheDir);
    }

    @Override
    protected Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Optional.class, new OptionalLocalDateTimeCreator())
                .create();
    }

    private class OptionalLocalDateTimeCreator implements InstanceCreator<Optional<?>> {
        @SuppressWarnings("unchecked")
        public Optional<?> createInstance(Type type) {
            // No need to use a parameterized list since the actual instance will have the raw type anyway.
            return Optional.of(new LocalDateTime());
        }
    }

    @Override
    protected Type getType() {
        return new TypeToken<SerializedLine<List<Event>>>() {}.getType();
    }


}
