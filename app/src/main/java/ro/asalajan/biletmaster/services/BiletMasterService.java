package ro.asalajan.biletmaster.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import ro.asalajan.biletmaster.activities.RxUtils;
import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.parser.BiletMasterParser;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

public class BiletMasterService {

    private static final String ROOT = "http://biletmaster.ro";
    private static final String LOCATIONS_URL = ROOT + "/ron/AllPlaces/Minden_helyszin";

    private final BiletMasterParser parser;
    private final HttpGateway httpGateway;

    public BiletMasterService(BiletMasterParser parser, HttpGateway httpGateway) {
        this.parser = parser;
        this.httpGateway = httpGateway;
    }

    public Observable<List<Location>> getLocations() {
        return httpGateway.downloadWebPage(LOCATIONS_URL).map(parseLocations);
    }

    public Observable<List<Location>> getDistinctLocations(final List<String> distinctLocations) {
        return httpGateway
                .downloadWebPage(LOCATIONS_URL)
                .map(parseLocations)
                .map(locations -> Multimaps.index(locations, groupBy(distinctLocations)))
                .map((ImmutableListMultimap<String, Location> nameToLocations) -> {
                    List<Location> locations = new ArrayList<>();
                    for (String name : nameToLocations.keySet()) {
                        locations.add(merge(name, nameToLocations.get(name)));
                    }
                    return locations;
                });
    }

    @NonNull
    private Function<Location, String> groupBy(final List<String> locationNameKeys) {
        return location -> {
            for (String key : locationNameKeys) {
                if (location.getLocation().toUpperCase().contains(key.toUpperCase())) {
                    return key;
                }
            }
            return location.getLocation();
        };
    }

    private Location merge(String newName, ImmutableList<Location> locations) {
        List<Venue> venues = new ArrayList<>();
        for(Location loc : locations) {
            venues.addAll(loc.getVenues());
        }
        return new Location(newName, venues);
    }

    public Observable<List<Event>> getEventsForLocation(Location location) {
        List<Observable<List<Event>>> venues = new ArrayList<>();
        for (Venue v : location.getVenues()) {
            venues.add(Observable.just(v).flatMap(venue -> getEventsForVenue(venue)));
        }
        return Observable.zip(venues, new FuncN<List<Event>>() {
            @Override
            public List<Event> call(Object... args) {
                List<Event> allEvents = new ArrayList<Event>();
                for (Object o : args) {
                    List<Event> le = (List<Event>) o;
                    allEvents.addAll(le);
                }
                return allEvents;
            }
        });
    }

    public Observable<List<Event>> getEventsForVenue(Venue venue) {

        //TODO recover from parse exception
        return httpGateway.downloadWebPage(ROOT + venue.getUrl())
                .map(data -> parser.parseEvents(data))
                .onErrorResumeNext(Observable.empty());
    }

    private Func1<? super InputStream, List<Location>> parseLocations =
        new Func1<InputStream, List<Location>>() {
            @Override
            public List<Location> call(InputStream inputStream) {
                return parser.parseLocations(inputStream);
            }
        };
}
