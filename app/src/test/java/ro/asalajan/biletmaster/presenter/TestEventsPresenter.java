package ro.asalajan.biletmaster.presenter;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.presenters.EventsPresenter;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterHelper;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterServiceImpl;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEventsPresenter {

    private EventsPresenter presenter;

    private BiletMasterService service;
    private Location location;
    private Location location2;

    private static TestScheduler mainThread = new TestScheduler();
    private static TestScheduler ioThread = new TestScheduler();
    private static TestScheduler computationThread = new TestScheduler();
    private static TestScheduler newThread = new TestScheduler();

    static {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return ioThread;
            }

            @Override
            public Scheduler getComputationScheduler() {
                return computationThread;
            }

            @Override
            public Scheduler getNewThreadScheduler() {
                return newThread;
            }

        });

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return mainThread;
            }


        });
    }

    private List<List<Location>> viewLocations;
    private List<List<Event>> viewEvents;

//        @After
//    public void tearDown() {
//        RxAndroidPlugins.getInstance().reset();
//    }


    @Before
    public void setup() {
        service = mock(BiletMasterService.class);
        location = new Location("location1", newArrayList(
                new Venue("venue1", "venue1Url"))
        );
        location2 = new Location("location2", newArrayList(
                new Venue("venue2", "venue2Url"))
        );

        viewLocations = new ArrayList<>();
        viewEvents = new ArrayList<>();
    }

    @Test
    public void getLocations() {
        when(service.getDistinctLocations(eq(BiletMasterHelper.DISTINCT_LOCATIONS)))
                .thenReturn(Observable.just(expectedLocations()));

        presenter = new EventsPresenter(service);
        EventsView view = getEventsView();
        presenter.setView(view);

        mainThread.advanceTimeBy(1, TimeUnit.SECONDS);

        Assert.assertEquals(expectedLocations(), viewLocations.get(0));

    }

    @Test
    public void getEventsForSelectedLocation() {
        when(service.getDistinctLocations(eq(BiletMasterHelper.DISTINCT_LOCATIONS)))
                .thenReturn(Observable.just(expectedLocations()));

        when(service.getEventsForLocation(eq(location)))
                .thenReturn(Observable.just(expectedEvents()));

        when(service.getEventsForLocation(eq(location2)))
                .thenReturn(Observable.just(expectedEvents2()));

        presenter = new EventsPresenter(service);
        EventsView view = getEventsView();

        presenter.setView(view);

        mainThread.advanceTimeBy(1, TimeUnit.SECONDS);

        Assert.assertEquals(expectedEvents(), viewEvents.get(0));
        Assert.assertEquals(expectedEvents2(), viewEvents.get(1));
    }

    private List<Event> expectedEvents() {
        return newArrayList(
                new Event("event1", "artist1", null, null, false, null),
                new Event("event2", "artist2", null, null, false, null)
        );
    }

    private List<Event> expectedEvents2() {
        return newArrayList(
                new Event("event3", "artist3", null, null, false, null),
                new Event("event4", "artist4", null, null, false, null)
        );
    }

    @NonNull
    private List<Location> expectedLocations() {

        return newArrayList(
                location, location2
        );
    }

    @NonNull
    private EventsView getEventsView() {
        return new EventsView() {


            @Override
            public void setLocations(List<Location> locations) {
                TestEventsPresenter.this.viewLocations.add(locations);
            }


            @Override
            public void setEvents(List<Event> events) {
                TestEventsPresenter.this.viewEvents.add(events);
            }

            @Override
            public Observable<Location> getSelectedLocation() {
                return Observable.just(location, location2);
            }

            @Override
            public void onCreate() {

            }

            @Override
            public void onBackground() {

            }

            @Override
            public void onForeground() {

            }

            @Override
            public void onDestroy() {

            }
        };
    }
}
