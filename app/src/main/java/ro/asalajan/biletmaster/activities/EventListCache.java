package ro.asalajan.biletmaster.activities;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;

public class EventListCache extends FilePersistableCache<List<Event>> {

    public EventListCache(File externalCacheDir) {
        super(externalCacheDir);
    }

    @Override
    protected Type getType() {
        return new TypeToken<SerializedLine<List<Event>>>() {}.getType();
    }
}
