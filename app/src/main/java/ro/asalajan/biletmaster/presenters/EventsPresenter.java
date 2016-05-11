package ro.asalajan.biletmaster.presenters;

import android.support.annotation.NonNull;
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
                .map(locations -> initEmptyLocation(locations))

                .observeOn(AndroidSchedulers.mainThread())

                .retryWhen(errors -> errors.doOnNext((e) -> {
                            Log.e("retry", "in retry locations.....");

                            //TODO only show offline if fragment not already added (implement in showOffline())
                            env.isOnline().filter(b -> FALSE.equals(b)).doOnCompleted(() -> this.view.showOffline()).subscribe();
                            //TODO show cached even when no internet
                        })

                                .zipWith(view.getNoInternetView().retry()
                                        .doOnNext(click -> {
                                            Log.e("retry", "retry locations clicked !!!!!!!!!!!!!!!!!");
                                            this.view.hideOffline();
                                        }), (throwable, click) -> click)
                )


                .subscribe(
                        locations -> {
                            view.setLocations(locations);
                            Log.e("init", "set locations.....");
                        },
                        t -> t.printStackTrace(),
                        () -> onSelect(view.getSelectedLocation()));

    }

    @NonNull
    private List<Location> initEmptyLocation(List<Location> locations) {
        for (Location loc : locations) {
            if (loc.getLocation() == null || loc.getLocation().isEmpty()) {
                loc.setLocation("Alte zone");
            }
        }
        return locations;
    }

    private void onSelect(Observable<Location> selected) {
        Log.e("eventsCompleted", "onSelect");
        eventsSub = selected
                //.replay(1).autoConnect(1)

                .flatMap(location -> biletService.getEventsForLocation(location))
                .observeOn(AndroidSchedulers.mainThread())


                .retryWhen(errors -> errors.doOnNext((e) -> {

                            Log.e("retry", "in retry events.....");
                            env.isOnline().filter(b -> FALSE.equals(b)).doOnCompleted(() -> this.view.showOffline()).subscribe();
                        })
                                .zipWith(view.getNoInternetView().retry()
                                                .doOnNext(click -> {
                                                    Log.e("retry", "retry events clicked !!!!!!!!!!!!!!!!!");
                                                    this.view.hideOffline();
                                                })
                                        ,
                                        (throwable, click) -> click)
                )


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
