package ro.asalajan.biletmaster.android.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.android.AndroidEnvironment;
import ro.asalajan.biletmaster.android.adapter.EventAdapter;
import ro.asalajan.biletmaster.android.adapter.LocationAdapter;
import ro.asalajan.biletmaster.android.fragments.NoInternetFragment;
import ro.asalajan.biletmaster.cache.EventListCache;
import ro.asalajan.biletmaster.cache.LocationCache;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import ro.asalajan.biletmaster.presenters.Environment;
import ro.asalajan.biletmaster.presenters.EventsPresenter;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterServiceImpl;
import ro.asalajan.biletmaster.services.biletmaster.CachedBiletMasterService;
import ro.asalajan.biletmaster.cache.FilePersistableCache;
import ro.asalajan.biletmaster.gateways.HttpGateway;
import ro.asalajan.biletmaster.view.EventsView;
import ro.asalajan.biletmaster.view.NoInternetView;
import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;

public class EventsActivity extends Activity implements EventsView {

    private BiletMasterService biletService;

    private EventsPresenter presenter;

    private Spinner spinner;
    private LocationAdapter locationAdapter;
    private EventAdapter eventAdapter;
    FilePersistableCache<List<Event>> eventCache;
    FilePersistableCache<List<Location>> locationCache;
    private static String name = "EventsActivity";
    private List<String> distinctLocationNames;
    private ListView listView;

    Environment env;
    private FragmentManager fragmentManager;
    private NoInternetFragment noInternetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onViewCreate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(name, "focus changed: " + hasFocus);
        if (hasFocus) {
            onForeground();
        } else {
            onBackground();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onViewDestroy();
    }

    @Override
    public void onViewDestroy() {
        Log.e(name, "on destroy");
        presenter.removeView();
        Log.d(name, "onDestroy: removed view");
    }

    @Override
    public void onViewCreate() {
        JodaTimeAndroid.init(this);
        fragmentManager = getFragmentManager();
        noInternetFragment = new NoInternetFragment();
        env = new AndroidEnvironment(getApplicationContext());

        setContentView(R.layout.activity_events);

        eventCache = new EventListCache(getExternalCacheDir());
        locationCache = new LocationCache(getExternalCacheDir());

        biletService = new CachedBiletMasterService(
                new BiletMasterServiceImpl(new BiletMasterParserImpl(), new HttpGateway()),
                eventCache, locationCache
        );

        createLocationSpinner();
        createEventsFromSpinnerSelection();

        initPresenter();
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

    private void initPresenter() {
        if (presenter == null) {  //TODO save state of the presenter!
            loadCaches();
            distinctLocationNames = Arrays.asList(getResources().getStringArray(R.array.distinct_location_names));
            presenter = new EventsPresenter(env, biletService, distinctLocationNames);

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
        Log.e(name, "show offline fragment !!!!!!!!!");
        setEvents(Collections.emptyList());
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.add(R.id.eventsActivity, noInternetFragment);
        tx.commit();
//        Log.e(name, "show offline toast");
//        Toast.makeText(getApplicationContext(), "No internet connection available.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideOffline() {
        if (noInternetFragment != null) {
            FragmentTransaction tx = fragmentManager.beginTransaction();
            tx.remove(noInternetFragment);
            tx.commit();
        }
    }

    public void triggerSelect() {
        int selectedItemPosition = spinner.getSelectedItemPosition();
        if (selectedItemPosition != Spinner.INVALID_POSITION) {
            spinner.setSelection(selectedItemPosition, true);
        } else {
            //spinner.setSelection(0);
            spinner.setSelection(1, true);
        }
    }

    @Override
    public void showError() {
        Log.e(name, "show error toast");
        Toast.makeText(getApplicationContext(), "There was an error. Please retry.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public NoInternetView getNoInternetView() {
        return noInternetFragment;
    }

    @Override
    public void setLocations(List<Location> locations) {
        locationAdapter.clear();
        locationAdapter.addAll(locations);
       // triggerSelect();
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

            int selectedItemPosition = spinner.getSelectedItemPosition();
            if (selectedItemPosition == spinner.INVALID_POSITION) {
                selectedItemPosition = 0;
            }
            subscriber.onNext(locationAdapter.getItem(selectedItemPosition));
        });
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


}