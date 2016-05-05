package ro.asalajan.biletmaster.presenters;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

import static java.lang.Boolean.*;

public class EventsPresenter implements Presenter<EventsView>  {

    private static String name = "EventPresenter";

    private EventsView view;

    private Environment env;
    private BiletMasterService biletService;
    private List<String> distinctLocationNames;

    private Subscription locationsSub, eventsSub;

    public EventsPresenter(Environment env, BiletMasterService biletService, List<String> distinctLocationNames) {
        this.env = env;
        this.biletService = biletService;
        this.distinctLocationNames = distinctLocationNames;
    }

    @Override
    public void setView(EventsView view) {
        this.view = view;
        init();
    }

    private void init() {
        Log.e(name, ">>>>>>>init presenter");
        locationsSub =  biletService.getDistinctLocations(distinctLocationNames)
                .map(locations -> {
                    for (Location loc : locations) {
                        if (loc.getLocation() == null || loc.getLocation().isEmpty()) {
                            loc.setLocation("Alte zone");
                        }
                    }
                    return locations;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    view.setEvents(Collections.emptyList());
                    env.isOnline().filter(b -> TRUE.equals(b)).doOnCompleted(() -> this.view.showError()).subscribe();
                    env.isOnline().filter(b -> FALSE.equals(b)).doOnCompleted(() -> this.view.showOffline()).subscribe();
                })
                .retry(4)
                .subscribe(locations -> view.setLocations(locations),
                        t -> t.printStackTrace());

        onSelect(view.getSelectedLocation());
    }

    private void onSelect(Observable<Location> selected) {
        eventsSub = selected
                .flatMap(location -> biletService.getEventsForLocation(location))
                .observeOn(AndroidSchedulers.mainThread())

                .doOnError(throwable -> {
                    view.setEvents(Collections.emptyList());
                    env.isOnline().filter(b -> TRUE.equals(b)).doOnCompleted(() -> this.view.showError()).subscribe();
                    env.isOnline().filter(b -> FALSE.equals(b)).doOnCompleted(() -> this.view.showOffline()).subscribe();
                })

                .retry(4)
                .subscribe(events -> view.setEvents(events),
                        t -> t.printStackTrace());
    }


    @Override
    public void removeView() {
        eventsSub.unsubscribe();
        locationsSub.unsubscribe();
        view = null;
    }
}
