package ro.asalajan.biletmaster.services.biletmaster;

import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import rx.Observable;

public interface BiletMasterService {

    Observable<List<Location>> getLocations();

    Observable<List<Location>> getDistinctLocations(final List<String> distinctLocations);

    Observable<List<Event>> getEventsForLocation(Location location);

    Observable<List<Event>> getEventsForVenue(Venue venue);

}