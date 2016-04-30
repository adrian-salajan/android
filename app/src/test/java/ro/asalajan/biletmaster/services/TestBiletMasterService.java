package ro.asalajan.biletmaster.services;

import junit.framework.Assert;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.parser.BiletMasterParser;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBiletMasterService {

    private BiletMasterParser parser;
    private HttpGateway httpGateway;

    BiletMasterServiceImpl service;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");


//    static {
//        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
//            @Override
//            public Scheduler getIOScheduler() {
//                return Schedulers.immediate();
//            }
//
//            @Override
//            public Scheduler getComputationScheduler() {
//                return Schedulers.immediate();
//            }
//
//            @Override
//            public Scheduler getNewThreadScheduler() {
//                return Schedulers.immediate();
//            }
//
//
//
//        });
//
//        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
//            @Override
//            public Scheduler getMainThreadScheduler() {
//                return Schedulers.immediate();
//            }
//
//        });
//    }

    @Before
    public void setup() throws UnsupportedEncodingException {
        parser = new BiletMasterParserImpl();
        httpGateway = mock(HttpGateway.class);
        service = new BiletMasterServiceImpl(parser, httpGateway);
    }

    @Test
    public void getLocations() throws UnsupportedEncodingException {
        ByteArrayInputStream webpage = new ByteArrayInputStream(
                (   "<div class=\"stacktitle\">a</div>" +
                        "<div class=\"stacktitle\">b</div>" +
                        "<p><div class=\"stacktitle\">c</div></p>").getBytes("UTF-8")
        );

        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(webpage));

        Observable<List<Location>> locations = service.getLocations();
        TestSubscriber<List<Location>> probe = new TestSubscriber<>();

        locations.subscribe(probe);

        probe.assertNoErrors();
        probe.assertValue(newArrayList(
                new Location("a", Collections.emptyList()),
                new Location("b", Collections.emptyList()),
                new Location("c", Collections.emptyList())
        ));
    }

//    @Test
//    public void getLocationsAfterError() throws UnsupportedEncodingException {
//        ByteArrayInputStream webpage = new ByteArrayInputStream(
//                (   "<div class=\"stacktitle\">a</div>" +
//                        "<div class=\"stacktitle\">b</div>" +
//                        "<p><div class=\"stacktitle\">c</div></p>").getBytes("UTF-8")
//        );
//
//        PublishSubject<InputStream> subject = PublishSubject.create();
//
//        when(httpGateway.downloadWebPage(anyString()))
//               // .thenReturn(Observable.<InputStream>just(webpage));
//        .thenReturn(subject);
//
//        Observable<List<Location>> locations = cachedService.getLocations();
//        TestSubscriber<List<Location>> probe = new TestSubscriber<>();
//        locations.subscribe(probe);
//
//        subject.onError(new RuntimeException("test exception"));
//        subject.onNext(webpage);
//
//        probe.assertNoErrors();
//        probe.assertValue(newArrayList(
//                new Location("a", Collections.emptyList()),
//                new Location("b", Collections.emptyList()),
//                new Location("c", Collections.emptyList())
//        ));
//    }

    @Test
    public void getLocationsWithVenues() throws UnsupportedEncodingException {
        InputStream allLocations = readResource("allLocations.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(allLocations));

        Observable<List<Location>> locations = service.getLocations();
        TestSubscriber<List<Location>> probe = new TestSubscriber<>();

        locations.subscribe(probe);

        probe.assertNoErrors();
        probe.assertValue(newArrayList(
                new Location("location1", newArrayList(
                        new Venue("venue1", "/venue1Url"),
                        new Venue("venue2", "/venue2Url"),
                        new Venue("venue3", "/venue3Url"))),
                new Location("location2", newArrayList(
                        new Venue("venue4", "/venue4Url"),
                        new Venue("venue5", "/venue5Url"),
                        new Venue("venue6", "/venue6Url")))
        ));
    }

    @Test
    public void mergeVenuesByDistinctLocations() throws UnsupportedEncodingException {
        InputStream allLocations = readResource("duplicateLocations.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(allLocations));


        Observable<List<Location>> locations = service.getDistinctLocations(newArrayList("location1", "location2"));
        TestSubscriber<List<Location>> probe = new TestSubscriber<>();

        locations.subscribe(probe);

        probe.assertNoErrors();
        probe.assertValue(newArrayList(
                new Location("location1", newArrayList(
                        new Venue("venue1", "/venue1Url"),
                        new Venue("venue2", "/venue2Url"),
                        new Venue("venue3", "/venue3Url"),
                        new Venue("venue4", "/venue4Url"),
                        new Venue("venue5", "/venue5Url"),
                        new Venue("venue6", "/venue6Url"))),
                new Location("location2", newArrayList(
                        new Venue("venue7", "/venue7Url"),
                        new Venue("venue8", "/venue8Url"),
                        new Venue("venue9", "/venue9Url")))
        ));
    }

    @Test
    public void getEventsForVenue() {
        InputStream allLocations = readResource("eventsForVenue.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(allLocations));

        Venue venue = new Venue("asd", "url");
        Observable<List<Event>> events = service.getEventsForVenue(venue);

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        events.subscribe(probe);

        probe.assertNoErrors();
        List<Event> result = probe.getOnNextEvents().get(0);

        Assert.assertEquals("Unexpected number of events", 8, result.size());

        Assert.assertEquals("Unexpected title", "Aproape de tine", result.get(0).getName());
        Assert.assertEquals("Unexpected artist", "artist1", result.get(0).getArtist());
        Assert.assertEquals("Unexpected venue", venue, result.get(0).getVenue());

        Assert.assertEquals("Unexpected title", "Aproape de tine", result.get(1).getName());
        Assert.assertEquals("Unexpected artist", null, result.get(1).getArtist());
        Assert.assertEquals("Unexpected venue", venue, result.get(1).getVenue());
    }

    @Test
    public void readFullEventInfo() {
        InputStream fullEvent = readResource("fullEvent.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(fullEvent));

        Observable<List<Event>> events = service.getEventsForVenue(new Venue("asd", "url"));

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        events.subscribe(probe);

        probe.assertNoErrors();
        List<Event> result = probe.getOnNextEvents().get(0);

        Assert.assertEquals("Unexpected number of events", 1, result.size());

        Event event = result.get(0);
        Assert.assertEquals("Unexpected title", "title", event.getName());
        Assert.assertEquals("Unexpected artist", "artist", event.getArtist());
        Assert.assertEquals("Unexpected room", "room", event.getRoom());
    }

    @Test
    public void readFullEventWithMissingDate() {
        InputStream fullEvent = readResource("eventNoDate.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(fullEvent));

        Observable<List<Event>> events = service.getEventsForVenue(new Venue("asd", "url"));

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        events.subscribe(probe);

        probe.assertNoErrors();
        List<Event> result = probe.getOnNextEvents().get(0);

        Assert.assertEquals("Unexpected number of events", 1, result.size());

        Event event = result.get(0);
        Assert.assertEquals("Unexpected title", "title", event.getName());
        Assert.assertEquals("Unexpected artist", "artist", event.getArtist());
        Assert.assertEquals("Unexpected room", "room", event.getRoom());

        Assert.assertFalse(event.getDateTime().isPresent());
    }

    @Test
    public void getEventsWithDateForVenue() {
        InputStream allLocations = readResource("eventsForVenue.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(allLocations));

        Observable<List<Event>> eventsObs = service.getEventsForVenue(new Venue("asd", "url"));

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        eventsObs.subscribe(probe);

        probe.assertNoErrors();
        List<List<Event>> result = probe.getOnNextEvents();
        Assert.assertEquals("Unexpected number of events", 8, result.get(0).size());

        List<Event> events = result.get(0);

        Assert.assertEquals("19.04.2016 10:30", fmt.print(events.get(0).getDateTime().get()));
        Assert.assertEquals("19.04.2016 12:30", fmt.print(events.get(1).getDateTime().get()));
        Assert.assertEquals("20.04.2016 10:30", fmt.print(events.get(2).getDateTime().get()));
    }

    //TODO: handle & test for missing Event.artist & Event.room & Event.date

    //TODO: try hamcrest for UT

    @Test
    public void getEventsForLocation() {
        Location loc = new Location("test", newArrayList(new Venue("v1", "url1"),new Venue("v2", "url2")));

        when(httpGateway.downloadWebPage(Mockito.anyString())).thenReturn(
                Observable.just(readResource("eventsForVenue1.html")),
                Observable.just(readResource("eventsForVenue2.html"))
        );

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        Observable
                .just(loc)
                .flatMap(location -> service.getEventsForLocation(location))
                .subscribe(probe);


        probe.assertNoErrors();

        //assert the next event containts contents of all lists
        List<Event> events = probe.getOnNextEvents().get(0);

        //first list
        Assert.assertEquals("Unexpected title", "event1", events.get(0).getName());
        Assert.assertEquals("Unexpected artist", "artist1", events.get(0).getArtist());
        //second list
        Assert.assertEquals("Unexpected title", "event2", events.get(1).getName());
        Assert.assertEquals("Unexpected artist", "artist2", events.get(1).getArtist());
    }

    private InputStream readResource(String res) {
        return this.getClass().getClassLoader().getResourceAsStream(res);
    }
}
