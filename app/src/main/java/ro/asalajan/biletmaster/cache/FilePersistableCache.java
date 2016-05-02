package ro.asalajan.biletmaster.cache;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import org.joda.time.Hours;
import org.joda.time.Instant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class FilePersistableCache<T> extends PersistableCache {

    private static final String name = "FilePersistableCache";
    private static final Hours CACHE_PERIOD = Hours.hours(12);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private File cacheFile;
    private Map<Integer, Instant> timestampById;

    private Gson gson;
    private final Type serializedLineType;

    public FilePersistableCache(DataCache innerCache, File externalCacheDir) {
        super(innerCache);
        cacheFile = new File(externalCacheDir, "cacheFile");
        try {
            initNewCache();
        } catch (IOException e) {
            Log.e(name, e.toString());
            e.printStackTrace();
        }
        serializedLineType = getType();
        timestampById = new HashMap<>();
        Log.e(name, "FilePersistableCache created");

        gson = getGson();
    }

    protected abstract Gson getGson();
    protected abstract Type getType();

    private void initNewCache() throws IOException {
        if (cacheFile.createNewFile()) {
            Log.d(name, ">>>> inited first time, created cacheFile file: " + cacheFile.getAbsolutePath());
        }
    }

    @Override
    public void put(int id, Object o) {
        super.put(id, o);
        timestampById.put(id, Instant.now());
        Log.d(name, ">>>> put in cacheFile: " + id + " - " + o);

    }

    @Override
    public void save() {
        Log.e(name, "SAVING FILE CACHE:");
        if (cacheFile.delete()) {
            try {
                cacheFile.createNewFile();
                Log.d(name, ">>>> created new cacheFile file before saving: " + cacheFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile, true))) {
            writeData(writer, innerCache.getContents());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeData(BufferedWriter writer, ImmutableMap<Integer, Object> data) throws IOException {
        for (Integer id : data.keySet()) {
            T cached = (T) innerCache.get(id);
            write(id, cached, writer);
        }
    }

    private void write(Integer id, T cached, BufferedWriter writer) throws IOException {
        SerializedLine<T> line = getNewLine();
        line.id = id;
        Log.d(name, "<<<<<< trying to write to file: " + id + " - ");
        line.timestamp = timestampById.get(id).getMillis();
        line.data = cached;

        writer.write(gson.toJson(line, serializedLineType));
        writer.write(LINE_SEPARATOR);

    }

    @Override
    public void load() {
        Log.e(name, "LOADING FILE CACHE");
        Instant now = Instant.now();
        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadLine(now, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLine(Instant now, String line) {
        SerializedLine<T> serializedLine = gson.fromJson(line, serializedLineType);
        Instant cachedAt = new Instant(serializedLine.timestamp);
        if (isDataValid(now, cachedAt)) {
            innerCache.put(serializedLine.id, serializedLine.data);
            timestampById.put(serializedLine.id, cachedAt);
        }
    }

    private boolean isDataValid(Instant now, Instant cachedAt) {
        return Hours.hoursBetween(cachedAt, now).isLessThan(CACHE_PERIOD);
    }

    @Override
    public void clear() {
        Log.e(name, "CLEARILE FILE CACHE");
        super.clear();
        timestampById.clear();
    }

    SerializedLine<T> getNewLine() {
        return new SerializedLine<T>();
    }

    protected class SerializedLine<V> {
        public int id;
        public long timestamp;
        public V data;
    }

}
