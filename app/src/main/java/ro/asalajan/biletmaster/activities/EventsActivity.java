package ro.asalajan.biletmaster.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.activities.adapter.EventAdapter;
import ro.asalajan.biletmaster.activities.adapter.LocationAdapter;
import ro.asalajan.biletmaster.cache.EventListCache;
import ro.asalajan.biletmaster.cache.LocationCache;
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
    FilePersistableCache<List<Event>> eventCache;
    FilePersistableCache<List<Location>> locationCache;
    private String name;
    private List<String> distinctLocationNames;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_events);

        eventCache = new EventListCache(getExternalCacheDir());
        locationCache = new LocationCache(getExternalCacheDir());

        biletService = new CachedBiletMasterService(
                            new BiletMasterServiceImpl(new BiletMasterParserImpl(), new HttpGateway()),
                            eventCache, locationCache
        );

        createLocationSpinner();
        createEventsFromSpinnerSelection();

        if (presenter == null) {  //TODO save state of the presenter!
            name = "EventsActivity";
            loadCaches();
            distinctLocationNames = Arrays.asList(getResources().getStringArray(R.array.distinct_location_names));
            presenter = new EventsPresenter(biletService, distinctLocationNames);


            presenter.setView(this);
            Log.d(name, "onCreate: loaded cache, created new presenter");
        }
    }

    private void loadCaches() {
        eventCache.load();
        locationCache.load();
    }

    @Override
    public void showOffline() {
        Log.e(name, "show offline toast");
        Toast.makeText(getApplicationContext(), "No internet connection available.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<Boolean> isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(name, "is online: " + (networkInfo != null && networkInfo.isConnected()));
        return Observable.just(networkInfo != null && networkInfo.isConnected());
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
        listView = (ListView) findViewById(R.id.eventsListView);
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
    public Observable<DragEvent> listDraggs() {
        return Observable.create(subscriber -> {
            listView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    subscriber.onNext(event);
                    return true;
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
        saveCaches();
        Log.d(name, "onStop: saved cache");
    }

    private void saveCaches() {
        eventCache.save();
        locationCache.save();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(name, "on destroy");
        presenter.removeView();
        Log.d(name, "onDestroy: removed view");
    }
}