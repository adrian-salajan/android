package ro.asalajan.biletmaster.services;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.parser.BiletMasterParser;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestBiletMasterService {

    private BiletMasterParser parser;
    private HttpGateway httpGateway;

    BiletMasterService service;

    @Before
    public void setup() throws UnsupportedEncodingException {
        parser = new BiletMasterParserImpl();
        httpGateway = mock(HttpGateway.class);
        service = new BiletMasterService(parser, httpGateway);
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
        probe.assertValue(Lists.newArrayList(
                new Location("a", Collections.emptyList()),
                new Location("b", Collections.emptyList()),
                new Location("c", Collections.emptyList())
        ));
    }

    @Test
    public void getLocationsWithVenues() throws UnsupportedEncodingException {
        InputStream allLocations = readResource("allLocations.html");
        when(httpGateway.downloadWebPage(anyString()))
                .thenReturn(Observable.<InputStream>just(allLocations));

        Observable<List<Location>> locations = service.getLocations();
        TestSubscriber<List<Location>> probe = new TestSubscriber<>();

        locations.subscribe(probe);

        probe.assertNoErrors();
        probe.assertValue(Lists.newArrayList(
                new Location("location1", Lists.newArrayList(
                        new Venue("venue1", "/venue1Url"),
                        new Venue("venue2", "/venue2Url"),
                        new Venue("venue3", "/venue3Url"))),
                new Location("location2", Lists.newArrayList(
                        new Venue("venue4", "/venue4Url"),
                        new Venue("venue5", "/venue5Url"),
                        new Venue("venue6", "/venue6Url")))
        ));
    }

    private InputStream readResource(String res) {
        return this.getClass().getClassLoader().getResourceAsStream(res);
    }
}
