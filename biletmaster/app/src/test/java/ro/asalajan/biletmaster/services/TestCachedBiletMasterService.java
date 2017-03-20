package ro.asalajan.biletmaster.services;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.services.biletmaster.CachedBiletMasterService;
import ro.asalajan.biletmaster.cache.DataCache;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TestCachedBiletMasterService {


    private BiletMasterService innerService;
    private BiletMasterService cachedService;
    private DataCache cache;

    private Location location;
    private Venue venue1;
    private Venue venue2;


    @Before
    public void setup() throws UnsupportedEncodingException {
        cache = Mockito.spy(new DataCache() {

            Map<Integer, Object> cache = new HashMap<>();

            @Override
            public void put(int id, Object o) {
                cache.put(id, o);
            }

            @Override
            public Object get(int id) {
                return cache.get(id);
            }

            @Override
            public ImmutableMap<Integer, Object> getContents() {
                return null;
            }

            @Override
            public void clear() {

            }
        });
        venue1 = new Venue("v1", "url1");
        venue2 = new Venue("v2", "url2");
        location = new Location("test", newArrayList(venue1, venue2));


        innerService = mock(BiletMasterService.class);
        cachedService = new CachedBiletMasterService(innerService, cache, cache);
    }

    @Test
    public void givenLocationQueryEventsWhereCached() {
        ArrayList<Event> expectedEvents = Lists.newArrayList(
                new Event("event1", "artist1", "room1", Optional.absent(), false, null, venue1),
                new Event("event2", "artist2", "room2", Optional.absent(), false, null, venue2)
        );

        when(innerService.getEventsForLocation(Mockito.eq(location)))
                .thenReturn(Observable.just(expectedEvents));

        TestSubscriber<List<Event>> probe = new TestSubscriber<>();

        Observable
                .just(location, location)
                .flatMap(location -> cachedService.getEventsForLocation(location))
                .subscribe(probe);


        probe.assertNoErrors();


        assertEquals(expectedEvents, probe.getOnNextEvents().get(0));
        assertEquals(expectedEvents, probe.getOnNextEvents().get(1));

        verify(innerService).getEventsForLocation(location);
        verifyNoMoreInteractions(innerService);
    }

    @Test
    public void distinctLocationsAreCached() {
        List<Location> expectedLocations = Lists.newArrayList(location);

        when(innerService.getDistinctLocations(Mockito.eq(Collections.emptyList())))
                .thenReturn(Observable.just(expectedLocations));



        TestSubscriber<List<Location>> probe1 = new TestSubscriber<>();
        cachedService.getDistinctLocations(Collections.emptyList())
            .subscribe(probe1);

        TestSubscriber<List<Location>> probe2 = new TestSubscriber<>();
        cachedService.getDistinctLocations(Collections.emptyList())
                .subscribe(probe2);

        probe1.assertNoErrors();
        probe2.assertNoErrors();

        assertEquals(expectedLocations, probe1.getOnNextEvents().get(0));
        assertEquals(expectedLocations, probe2.getOnNextEvents().get(0));

        verify(innerService).getDistinctLocations(Collections.emptyList());
        verifyNoMoreInteractions(innerService);

    }

}
