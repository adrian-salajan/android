package ro.asalajan.biletmaster.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.activities.adapter.EventAdapter;
import ro.asalajan.biletmaster.activities.adapter.LocationAdapter;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import ro.asalajan.biletmaster.presenters.EventsActivityPresenter;
import ro.asalajan.biletmaster.services.BiletMasterHelper;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.services.HttpGateway;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

public class EventsActivity extends Activity implements EventsView {

    private BiletMasterService biletService = new BiletMasterService(new BiletMasterParserImpl(), new HttpGateway());

    EventsActivityPresenter presenter;

    private Subscription locationsSub;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_events);

        //createLocationSpinner();
        spinner = (Spinner) findViewById(R.id.locationSpinner);
        createLocationSpinner(); //TODO save state of the presenter!

        createEventsFromSpinnerSelection();
    }

    private void createLocationSpinner() {

        LocationAdapter adapter = new LocationAdapter(this, new ArrayList<>());
        spinner.setAdapter(adapter);

        if (presenter == null) {
            Log.d("EventsActivity", "created new Presenter");
            presenter = new EventsActivityPresenter(new UpdateLocationsAndroidPort(adapter));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        presenter.setVisibility(hasFocus);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }




//    private void createLocationSpinner() {
//        locationsSub = biletService.getDistinctLocations(BiletMasterHelper.DISTINCT_LOCATIONS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(updateLocationSpinner());
//    }

//    public Observer<List<Location>> getLocationsObserver() {
//        return Observers.create(
//                locations -> {
//                    ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this, R.layout.support_simple_spinner_dropdown_item, locations);
//                    Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
//                    // Specify the layout to use when the list of choices appears
//                    //adapter.setDropDownViewResource(R.layout.);
//                    spinner.setAdapter(adapter);
//
//                },
//                throwable -> Log.e("error", throwable.toString())
//        );
//    }

//    private Observer<List<Location>> updateLocationSpinner() {
//        return Observers.create(
//            locations -> {
//                //ArrayAdapter<Location> adapter = new LocationAdapter(this, locations);
//                ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this, R.layout.support_simple_spinner_dropdown_item, locations);
//                Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
//                // Specify the layout to use when the list of choices appears
//                //adapter.setDropDownViewResource(R.layout.);
//                spinner.setAdapter(adapter);
//
//            },
//            throwable -> Log.e("error", throwable.toString())
//        );
//    }

//    private void createEventsFromSpinnerSelection() {
//        ListView listView =(ListView) findViewById(R.id.eventsListView);
//        EventAdapter eventAdapter = new EventAdapter(this, new ArrayList<Event>());
//        listView.setAdapter(eventAdapter);
//
//        Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
//        selections(spinner)
//                .flatMap(location -> biletService.getEventsForLocation(location))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(updateEventsListView(eventAdapter));
//
//    }

    private void createEventsFromSpinnerSelection() {
        ListView listView =(ListView) findViewById(R.id.eventsListView);
        EventAdapter eventAdapter = new EventAdapter(this, new ArrayList<Event>());
        listView.setAdapter(eventAdapter);


        selections(spinner)
                .flatMap(location -> biletService.getEventsForLocation(location))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateEventsListView(eventAdapter));

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



    @NonNull
    private Observer<List<Event>> updateEventsListView(final EventAdapter adapter) {


        return Observers.create(
                events -> {
                    Log.d(">>>>>>>>>>>>>", events.toString());
                    adapter.clear();
                    adapter.addAll(events);
                },
                throwable -> Log.e(">>>>>>>>>>>>> error", throwable.toString()),
                () -> Log.e(">>>>>>>>>>>>> obs", "complete"));

    }

    @Override
    public Subscriber<List<Location>> locationsSubscriber() {
        return null;
    }

    @Override
    public Subscriber<List<Event>> eventsSubscriber() {
        return null;
    }

    @Override
    public Observable<Location> getSelectedLocation() {
        return null;
    }
}