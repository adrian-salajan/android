package ro.asalajan.biletmaster.services;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestCachedBiletMasterService {


    BiletMasterService service;
    boolean gotFromCache = false;

    private Location loc;
    private ArrayList<Event> expected;
    private BiletMasterService innerService;

    @Before
    public void setup() throws UnsupportedEncodingException {
        DataCache cache = new DataCache() {

            Map<Integer, Object> cache = new HashMap<>();

            @Override
            public void put(int id, Object o) {
                cache.put(id, o);
            }

            @Override
            public Object get(int id) {
                gotFromCache = true;
                return cache.get(id);
            }

            @Override
            public void clear() {

            }
        };
        loc = new Location("test", newArrayList(new Venue("v1", "url1"),new Venue("v2", "url2")));
        innerService = mock(BiletMasterService.class);
        expected = Lists.newArrayList(
                new Event("event1", "artist1", "room1", Optional.absent(), false, null),
                new Event("event2", "artist2", "room2", Optional.absent(), false, null)
        );
        when(innerService.getEventsForLocation(Mockito.eq(loc)))
                .thenReturn(Observable.just(expected));
        service = new CachedBiletMasterService(innerService, cache);
    }

    @Test
    public void getEventsForLocation() {
        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        Observable
                .just(loc, loc)
                .flatMap(location -> service.getEventsForLocation(location))
                .subscribe(probe);


        probe.assertNoErrors();


        Assert.assertEquals(this.expected, probe.getOnNextEvents().get(0));
        Assert.assertEquals(this.expected, probe.getOnNextEvents().get(1));

        Mockito.verify(innerService).getEventsForLocation(loc);
        Mockito.verifyNoMoreInteractions(innerService);

        Assert.assertTrue(gotFromCache);

    }

}
