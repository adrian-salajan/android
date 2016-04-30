package ro.asalajan.biletmaster.activities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.services.cache.PersistableDataCache;

public abstract class FilePersistableCache<T> extends PersistableDataCache {

    private static final String name = "FilePersistableCache";
    private static final Hours CACHE_PERIOD = Hours.hours(12);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    //private final Type cachedType;
    private File cache;
    private Map<Integer, Instant> timestampById;
    private Gson gson;// = new Gson();
    private final Type serializedLineType;

    public FilePersistableCache(File externalCacheDir) {
        cache = new File(externalCacheDir, "cache");
        try {
            initNewCache();
        } catch (IOException e) {
            Log.e(name, e.toString());
            e.printStackTrace();
        }
        serializedLineType = getType();
        timestampById = new HashMap<>();
        Log.e(name, "FilePersistableCache created");

        gson = new GsonBuilder()
                .registerTypeAdapter(Optional.class, new OptionalLocalDateTimeCreator())
                .create();
    }

    protected abstract Type getType();

    private void initNewCache() throws IOException {
        if (!cache.createNewFile()) {
            //clear();
            //cache.createNewFile();
        } else {
            Log.d(name, ">>>> inited first time, created cache file: " + cache.getAbsolutePath());
        }
    }

    @Override
    public void put(int id, Object o) {
        timestampById.put(id, Instant.now());
        Log.d(name, ">>>> put in cache: " + id + " - " + o);
        super.put(id, o);

    }

    @Override
    public void save() {
        Log.e(name, "SAVING FILE CACHE:");
        if (cache.delete()) {
            try {
                cache.createNewFile();
                Log.d(name, ">>>> created new cache file before saving: " + cache.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cache, true))) {
            ImmutableMap<Integer, Object> data = innerCache.getContents();
            for (Integer id : data.keySet()) {
                T cached = (T) innerCache.get(id);
                write(id, cached, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        try (BufferedReader reader = new BufferedReader(new FileReader(cache))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadData(now, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData(Instant now, String line) {
        SerializedLine<T> serializedLine = gson.fromJson(line, serializedLineType);
        Instant then = new Instant(serializedLine.timestamp);
        if (isDataValid(now, then)) {
            T data = serializedLine.data;
            innerCache.put(serializedLine.id, data);
            timestampById.put(serializedLine.id, then);
        }
    }

    private boolean isDataValid(Instant now, Instant then) {
        return Hours.hoursBetween(then, now).isLessThan(CACHE_PERIOD);
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

    class SerializedLine<V> {
        public int id;
        public long timestamp;
        public V data;
        //public Class<T> dataType;

//        public SerializedLine(Class<T> dataType) {
//            this.dataType = dataType;
//        }
    }

    private class SerializedLineListOfEventsSerializer implements JsonSerializer<SerializedLine<List<Event>>> {
        public JsonElement serialize(SerializedLine src, Type typeOfSrc, JsonSerializationContext context) {
            SerializedLine<List<Event>> o = src;

            JsonArray array = new JsonArray();
            for (Event e : o.data) {
                JsonObject jsonEvent = eventToJson(e);
                array.add(jsonEvent);
            }

            JsonObject jsonEvent = new JsonObject();
            jsonEvent.add("id", new JsonPrimitive(src.id));
            jsonEvent.add("time", new JsonPrimitive(src.timestamp));
            jsonEvent.add("eventList", array);

            return jsonEvent;
        }

    @NonNull
    private JsonObject eventToJson(Event e) {
        JsonObject jsonEvent = new JsonObject();
        jsonEvent.addProperty("name", e.getName());
        jsonEvent.addProperty("artist", e.getArtist());
        jsonEvent.addProperty("room", e.getRoom());

        LocalDateTime dt = e.getDateTime().orNull();
        if (dt == null) {
            jsonEvent.add("date", JsonNull.INSTANCE);
        } else {
            jsonEvent.addProperty("date", dt.toString());
        }

        jsonEvent.add("venue", venueToJson(e.getVenue()));
        return jsonEvent;
    }

    @NonNull
    private JsonObject venueToJson(Venue venue) {
        JsonObject jsonVenue = new JsonObject();
        jsonVenue.addProperty("name", venue.getName());
        jsonVenue.addProperty("url", venue.getUrl());
        return jsonVenue;
    }
    }

    private class SerializedLineListOfEventsDeserializer implements JsonDeserializer<SerializedLine<List<Event>>> {
        public SerializedLine<List<Event>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            SerializedLine<List<Event>> line = new SerializedLine<>();
            line.id = json.getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
            line.timestamp = json.getAsJsonObject().getAsJsonPrimitive("time").getAsLong();
            line.data = getData(json.getAsJsonObject().getAsJsonArray("eventList"));
            return line;
        }

        private List<Event> getData(JsonArray eventList) {
            List<Event> list = new ArrayList<>();
            for (JsonElement event : eventList) {
                list.add(toEvent(event.getAsJsonObject()));
            }
            return list;
        }

        private Event toEvent(JsonObject event) {
            String name = event.getAsJsonPrimitive("name").getAsString();
            String artist = event.getAsJsonPrimitive("artist").getAsString();
            String room = event.getAsJsonPrimitive("room").getAsString();

            Optional<LocalDateTime> dateTime;
            if (event.getAsJsonPrimitive("date").isJsonNull()) {
               dateTime = Optional.absent();
            } else {
                dateTime = Optional.of(new LocalDateTime(event.getAsJsonPrimitive("date").getAsString()));
            }

            Venue venue = toVenue(event.getAsJsonObject("venue"));

            return new Event(name, artist, room, dateTime, false, null, venue);
        }

        private Venue toVenue(JsonObject venue) {
            String name = venue.getAsJsonPrimitive("name").getAsString();
            String url = venue.getAsJsonPrimitive("url").getAsString();
            return new Venue(name, url);
        }
    }

    class OptionalLocalDateTimeCreator implements InstanceCreator<Optional<?>> {
        @SuppressWarnings("unchecked")
        public Optional<?> createInstance(Type type) {
            // No need to use a parameterized list since the actual instance will have the raw type anyway.
            return Optional.of(new LocalDateTime());
        }
    }

}
