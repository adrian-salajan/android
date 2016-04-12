package ro.asalajan.biletmaster.services;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import ro.asalajan.biletmaster.Constants;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.parser.BiletMasterParser;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ro.asalajan.biletmaster.services.ServiceFunctions.dateToWebPage;
import static ro.asalajan.biletmaster.services.ServiceFunctions.dayToString;
import static ro.asalajan.biletmaster.services.ServiceFunctions.deltaToDate;
import static ro.asalajan.biletmaster.services.ServiceFunctions.parseEvents;

public class EventsService {

    private BiletMasterParser eventsParser;

    public EventsService(BiletMasterParser eventsParser) {
        this.eventsParser = eventsParser;
    }

    public Observable<List<Event>> downloadEvents(Calendar startingDate, Observable<Integer> dateDeltas) {
        return dateDeltas
                .map(deltaToDate(startingDate.get(Calendar.YEAR), startingDate.get(Calendar.MONTH), startingDate.get(Calendar.DATE)))
                .map(dayToString(Constants.DATE_FORMATER.get()))

                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(dateToWebPage())

                .filter(new Func1<InputStream, Boolean>() {
                    @Override
                    public Boolean call(InputStream inputStream) {
                        return inputStream != null;
                    }
                })
                .map(parseEvents(eventsParser));
    }
}
