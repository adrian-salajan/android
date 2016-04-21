package ro.asalajan.biletmaster.activities;

import android.util.Log;

import java.util.List;

import ro.asalajan.biletmaster.activities.adapter.LocationAdapter;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.presenters.UpdateLocationsPort;

public class UpdateLocationsAndroidPort implements UpdateLocationsPort {

    private LocationAdapter adapter;

    public UpdateLocationsAndroidPort(LocationAdapter adapter) {
        this.adapter = adapter;
    }



    @Override
    public void doUpdateLocations(List<Location> locations) {
        adapter.clear();
        adapter.addAll(locations);
    }

    @Override
    public void handleError(Throwable throwable) {
        Log.e("LocationsAndroidPort", throwable.toString());
    }
}
