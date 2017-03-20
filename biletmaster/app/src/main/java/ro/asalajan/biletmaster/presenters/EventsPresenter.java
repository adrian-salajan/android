package ro.asalajan.biletmaster.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

import static rx.Observable.*;

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
    //TODO show cached even when no internet
    private void init() {
        Log.e(name, ">>>>>>>init presenter");
        locationsSub = biletService.getDistinctLocations(distinctLocationNames)
                .map(locations -> initEmptyLocation(locations))

                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(errors -> errors.compose(retryIsClicked))


                .subscribe(
                        locations -> {
                            view.setLocations(locations);
                            Log.e("init", "set locations.....");
                        },
                        t -> t.printStackTrace(),
                        () -> onSelect(view.getSelectedLocation()));

    }

    private Transformer<Throwable, Object> retryIsClicked = errors -> errors
            .doOnNext(showOffline())
            .zipWith(getRetries(), (throwable, click) -> click)
            .doOnNext(hideOffline());

    private Action1<Object> hideOffline() {
        return (click) -> EventsPresenter.this.view.hideOffline();
    }

    @NonNull
    private Observable<Object> getRetries() {
        return view.getNoInternetView().retries();

    }

    @NonNull
    private Action1<Throwable> showOffline() {
        return (e) -> env.isOnline().toSingle().subscribe(isOnline -> {
            if (!isOnline) {
                EventsPresenter.this.view.showOffline();
            }
        });
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
        ConnectableObservable<Location> publish = selected.publish();
        eventsSub = publish
                //.replay(1).autoConnect(1)

                .flatMap(location -> biletService.getEventsForLocation(location)
                        .retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread()).compose(retryIsClicked)
                        .takeUntil(publish)
                        .doOnCompleted(() -> view.hideOffline()))
                )
                .observeOn(AndroidSchedulers.mainThread())


                .subscribe(events -> {
                    view.setEvents(events);
                    hideOffline();},
                        t -> t.printStackTrace());

        publish.connect();
    }


    @Override
    public void removeView() {
        eventsSub.unsubscribe();
        locationsSub.unsubscribe();
        view = null;
    }
}
