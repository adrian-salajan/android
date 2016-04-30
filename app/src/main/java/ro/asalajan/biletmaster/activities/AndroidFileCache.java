package ro.asalajan.biletmaster.activities;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.services.biletmaster.CachedBiletMasterService;
import ro.asalajan.biletmaster.services.cache.DataCache;

public abstract class AndroidFileCache implements DataCache {

    //private final Type type;
    private File externalCacheDir;
    private Gson gson = new Gson();

    private static final String name = "AndroidFileCache";
    private final File cache;

    public AndroidFileCache(File externalCacheDir) {
        this.externalCacheDir = externalCacheDir;
        cache = new File(externalCacheDir, "cache");
        try {
            initNewCache();
            Log.d(name, ">>>> created cache file: " + cache.getAbsolutePath());
        } catch (IOException e) {
            Log.e(name, e.toString());
        }
      //  type = new TypeToken<List<CachedBiletMasterService.SerializableEvent>>() {
     //   }.getType();
    }

    private void initNewCache() throws IOException {
        if (!cache.createNewFile()) {
            clear();
            cache.createNewFile();
            Log.d(name, "init new cache");
        }
    }

    @Override
    public void put(int id, Object o) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cache, true))) {

            String str = gson.toJson(o);
            String data = id + System.lineSeparator() + str + System.lineSeparator();
            Log.d(name, "writing events data" + id);
            writer.write(data);
            writer.flush();

        } catch (FileNotFoundException e) {
            Log.e(name, e.toString());
        } catch (IOException e) {
            Log.e(name, e.toString());
        }
    }

    @Override
    public Object get(int id) {

        try (BufferedReader reader = new BufferedReader(new FileReader(cache))) {
            String line;
            while ((line = reader.readLine()) != null && !line.equals(String.valueOf(id))) {
            }

            String data = reader.readLine();
            Log.d(name, "found cached data " + data);
            List<Event> o=null;// = gson.fromJson(data, type);
            Log.d(name, "deserialized data " + o);
            return o;

        } catch (FileNotFoundException e) {
            Log.e(name, e.toString());
        } catch (IOException e) {
            Log.e(name, e.toString());
        }


        return null;
    }

    @Override
    public void clear() {
        cache.delete();
    }
}
