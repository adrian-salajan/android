package ro.asalajan.biletmaster.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.services.HttpGateway;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

public class EventsActivity extends Activity {

    private BiletMasterService biletService = new BiletMasterService(new BiletMasterParserImpl(), new HttpGateway());

    private Subscription locationsSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        createLocationSpinner();

    }

    private void createLocationSpinner() {
        locationsSub = biletService.getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateLocationSpinner());
    }

    private Observer<List<Location>> updateLocationSpinner() {
        return Observers.create(
            locations -> {
                Log.d("update spinner thread", Thread.currentThread().getName());
                ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this,android.R.layout.simple_spinner_item, locations);
                Spinner spinner = (Spinner) findViewById(R.id.locationSpinner);
                // Specify the layout to use when the list of choices appears
                // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            },
            throwable -> Log.e("error", throwable.toString())
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationsSub.unsubscribe();
    }

    @NonNull
    private Observer<List<Location>> updateTextView(final TextView text) {
        return Observers.create(
            locations -> {
                StringBuilder builder = new StringBuilder();
                for (Location loc : locations) {
                    builder.append(loc.getLocation()).append(System.lineSeparator());
                }
                Log.d("observer thread", Thread.currentThread().getName());
                text.setText(builder.toString());
            },
            throwable -> Log.e("obs error", throwable.toString()),
            () -> Log.e("obs", "complete"));

    }
}