package ro.asalajan.biletmaster.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ro.asalajan.biletmaster.Constants;
import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.parser.EventsParserImpl;
import ro.asalajan.biletmaster.services.EventsService;
import ro.asalajan.biletmaster.services.Obs;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class EventsActivity extends Activity {

    Calendar calendar = new GregorianCalendar();

    EventsService eventsService = new EventsService(new EventsParserImpl());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        final TextView text = (TextView) findViewById(R.id.text);

        Calendar c = new GregorianCalendar();

        Observable<Integer> dayDeltas =  Obs.obsToSequence(Obs.Observable(text));
        eventsService.downloadEvents(calendar, dayDeltas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateTextView(text));
    }



    @NonNull
    private Observer<List<Event>> updateTextView(final TextView text) {
        return new Observer<List<Event>>() {
            @Override
            public void onCompleted() {
                Log.e("obs", "complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("obs error", e.toString());
            }

            @Override
            public void onNext(List<Event> events) {
                StringBuilder builder = new StringBuilder();
                for (Event e: events) {
                    builder.append(e.getLocation()).append(System.lineSeparator());
                }
                text.setText(builder.toString());
            }
        };
    }





}
