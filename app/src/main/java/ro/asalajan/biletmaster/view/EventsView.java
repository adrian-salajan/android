package ro.asalajan.biletmaster.view;

import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import rx.Observable;

public interface EventsView extends View {

    void setLocations(List<Location> locations);

    void setEvents(List<Event> events);

    Observable<Location> getSelectedLocation();

}