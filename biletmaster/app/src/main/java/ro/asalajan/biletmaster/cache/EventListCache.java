package ro.asalajan.biletmaster.cache;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;

public class EventListCache extends FilePersistableCache<List<Event>> {

    public EventListCache(File externalCacheDir) {
        super(new InMemoryDataCache(), externalCacheDir);
    }

    @Override
    protected String modelName() {
        return "events";
    }

    @Override
    protected Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Optional.class, new OptionalLocalDateTimeCreator())
                .registerTypeAdapter(Optional.class, new OptionalDateTimeSerializer())
                .registerTypeAdapter(Optional.class, new OptionalDateTimeDeserializer())
                .create();
    }
    private JsonPrimitive  ABSENT = new JsonPrimitive("absent");
    private class OptionalDateTimeSerializer implements JsonSerializer<Optional> {


        public JsonElement serialize(Optional src, Type typeOfSrc, JsonSerializationContext context) {
            if (src.isPresent()) {
                return new JsonPrimitive(src.get().toString());
            }
            return ABSENT;
        }
    }

    private class OptionalDateTimeDeserializer implements JsonDeserializer<Optional> {
        public Optional deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.equals(ABSENT)) {

                return Optional.absent();
            }
            System.out.println(">>>>>>> datetime: " + json.getAsJsonPrimitive().getAsString() );
            return Optional.of(LocalDateTime.parse(json.getAsJsonPrimitive().getAsString()));
        }
    }


    private class OptionalLocalDateTimeCreator implements InstanceCreator<Optional<?>> {
        @SuppressWarnings("unchecked")
        public Optional<?> createInstance(Type type) {
            // No need to use a parameterized list since the actual instance will have the raw type anyway.
            return Optional.absent();
        }
    }

    @Override
    protected Type getType() {
        return new TypeToken<SerializedLine<List<Event>>>() {}.getType();
    }


}
