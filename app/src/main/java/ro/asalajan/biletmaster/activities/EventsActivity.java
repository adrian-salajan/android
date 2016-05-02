package ro.asalajan.biletmaster.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.activities.adapter.EventAdapter;
import ro.asalajan.biletmaster.activities.adapter.LocationAdapter;
import ro.asalajan.biletmaster.cache.EventListCache;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import ro.asalajan.biletmaster.presenters.EventsPresenter;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterServiceImpl;
import ro.asalajan.biletmaster.services.biletmaster.CachedBiletMasterService;
import ro.asalajan.biletmaster.cache.FilePersistableCache;
import ro.asalajan.biletmaster.gateways.HttpGateway;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;

public class EventsActivity extends Activity implements EventsView {

    private BiletMasterService biletService;

    EventsPresenter presenter;

    private Spinner spinner;
    private LocationAdapter locationAdapter;
    private EventAdapter eventAdapter;
    private FilePersistableCache<?> cache;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_events);

        cache = new EventListCache(getExternalCacheDir());

        biletService = new CachedBiletMasterService(
                            new BiletMasterServiceImpl(new BiletMasterParserImpl(), new HttpGateway()),
                            cache
        );

        createLocationSpinner();
        createEventsFromSpinnerSelection();

        if (presenter == null) {  //TODO save state of the presenter!
            name = "EventsActivity";
            cache.load();
            presenter = new EventsPresenter(biletService);
            presenter.setView(this);
            Log.d(name, "onCreate: loaded cache, created new presenter");
            Log.e(name, "onCreate:");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(name, "on restart:");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(name, "onResume:");
    }

    @Override
    protected void onStop() {
         super.onStop();
        Log.e(name, "onStop:");
    }

    private void createLocationSpinner() {

        spinner = (Spinner) findViewById(R.id.locationSpinner);
        locationAdapter = new LocationAdapter(this, new ArrayList<>());
        spinner.setAdapter(locationAdapter);
    }



    private void createEventsFromSpinnerSelection() {
        ListView listView =(ListView) findViewById(R.id.eventsListView);
        eventAdapter = new EventAdapter(this, new ArrayList<>());
        listView.setAdapter(eventAdapter);
    }


    private Observable<Location> selections(final Spinner spinner) {
        return Observable.create(subscriber -> {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Location selectedLocation = (Location) spinner.getItemAtPosition(position);
                    subscriber.onNext(selectedLocation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        });
    }

    @Override
    public void setLocations(List<Location> locations) {
        locationAdapter.clear();
        locationAdapter.addAll(locations);
    }

    @Override
    public void setEvents(List<Event> events) {
        eventAdapter.clear();
        eventAdapter.addAll(events);
    }

    @Override
    public Observable<Location> getSelectedLocation() {
        return selections(spinner);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //  presenter.setVisibility(hasFocus);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onBackground() {

    }

    @Override
    public void onForeground() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(name, "on pause");
        cache.save();
        Log.d(name, "onStop: saved cache");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(name, "on destroy");
        presenter.removeView();
        Log.d(name, "onDestroy: removed view");
    }
}