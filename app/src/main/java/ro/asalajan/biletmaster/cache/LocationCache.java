package ro.asalajan.biletmaster.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import ro.asalajan.biletmaster.model.Location;

public class LocationCache extends FilePersistableCache<List<Location>> {

    public LocationCache(File externalCacheDir) {
        super(new InMemoryDataCache(), externalCacheDir);
    }

    @Override
    protected String modelName() {
        return "locations";
    }

    @Override
    protected Gson getGson() {
        return new Gson();
    }

    @Override
    protected Type getType() {
        return new TypeToken<SerializedLine<List<Location>>>() {}.getType();
    }
}
