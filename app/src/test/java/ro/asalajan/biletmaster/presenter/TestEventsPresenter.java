package ro.asalajan.biletmaster.presenter;

import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;
import ro.asalajan.biletmaster.presenters.EventsPresenter;
import ro.asalajan.biletmaster.services.BiletMasterHelper;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestEventsPresenter {

    private EventsPresenter presenter;

    private List<Location> viewLocations;
    private List<Event> viewEvents;
    private BiletMasterService service;
    private Location location;
    private Location location2;

    static {
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }

            @Override
            public Scheduler getComputationScheduler() {
                return Schedulers.immediate();
            }

            @Override
            public Scheduler getNewThreadScheduler() {
                return Schedulers.immediate();
            }

        });

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }

        });
    }

    private TestSubscriber<List<Location>> locationsSub;
    private TestSubscriber<List<Event>> eventsSub;

    //    @After
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

        locationsSub = new TestSubscriber<>();
        eventsSub = new TestSubscriber<>();
    }

    @Test
    public void getLocations() {
        when(service.getDistinctLocations(eq(BiletMasterHelper.DISTINCT_LOCATIONS)))
                .thenReturn(Observable.just(expectedLocations()));

        presenter = new EventsPresenter(service);
        EventsView view = getEventsView();

        presenter.setView(view);
        List<List<Location>> locations = locationsSub.getOnNextEvents();

        locationsSub.assertNoErrors();
        Assert.assertEquals(expectedLocations(), locations.get(0));
        locationsSub.assertCompleted();

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

        List<List<Event>> eventsInView = eventsSub.getOnNextEvents();

        presenter.setView(view);

        eventsSub.assertNoErrors();
        Assert.assertEquals(expectedEvents(), eventsInView.get(0));
        Assert.assertEquals(expectedEvents2(), eventsInView.get(1));
        eventsSub.assertCompleted();
    }

    private List<Event> expectedEvents() {
        return newArrayList(
                new Event("event1", "artist1", null, false, null),
                new Event("event2", "artist2", null, false, null)
        );
    }

    private List<Event> expectedEvents2() {
        return newArrayList(
                new Event("event3", "artist3", null, false, null),
                new Event("event4", "artist4", null, false, null)
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
            public Subscriber<List<Location>> locationsSubscriber() {
                return locationsSub;
            }

            //            @Override
//            public void setLocations(List<Location> locations) {
//                System.out.println(">>>>>>L>>>>>" + locations);
//                TestEventsPresenter.this.viewLocations = locations;
//            }

            public Subscriber<List<Event>> eventsSubscriber() {
                return eventsSub;
            }

//            @Override
//            public void setEvents(List<Event> events) {
//                System.out.println(">>>>>>E>>>>>" + events);
//                TestEventsPresenter.this.viewEvents = events;
//            }

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
