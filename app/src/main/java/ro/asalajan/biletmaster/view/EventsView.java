package ro.asalajan.biletmaster.view;

import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import rx.Observable;
import rx.Subscriber;

public interface EventsView extends View {

//    void setLocations(List<Location> locations);

    Subscriber<List<Location>> locationsSubscriber();

//    void setEvents(List<Event> events);

    Subscriber<List<Event>> eventsSubscriber();

    Observable<Location> getSelectedLocation();

}