package ro.asalajan.biletmaster.presenters;

import android.util.Log;

import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterHelper;
import ro.asalajan.biletmaster.services.biletmaster.BiletMasterService;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class EventsPresenter implements Presenter<EventsView>  {


    private BiletMasterService biletService;
    private List<String> distinctLocationNames;
    private EventsView view;
    private Subscription locationsSub;
    private Subscription eventsSub;
    private Subscription dragsWhileOfflineSub;

    private static String name = "EventPresenter";

    public EventsPresenter(BiletMasterService biletService, List<String> distinctLocationNames) {
        this.biletService = biletService;
        this.distinctLocationNames = distinctLocationNames;
    }

    @Override
    public void setView(EventsView view) {

        this.view = view;

        initView(this.view);

//        dragsWhileOfflineSub = view.listDraggs()
//                .takeUntil(view.isOnline().filter(isOnline -> Boolean.TRUE.equals(isOnline)))
//                .doOnCompleted(() -> initView(this.view))
//                .subscribe(
//                        dragEvent1 -> this.view.showOffline(),
//                        t -> t.printStackTrace());


    }

    private void initView(EventsView view) {

        Log.e(name, ">>>>>>>init view");

        locationsSub = biletService.getDistinctLocations(distinctLocationNames)
                .map(locations -> {
                    for (Location loc : locations) {
                        if (loc.getLocation() == null || loc.getLocation().isEmpty()) {
                            loc.setLocation("Alte zone");
                        }
                    }
                    return locations;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locations -> view.setLocations(locations),
                        t -> Log.d("activity locations", t.toString()));

        onSelect(view.getSelectedLocation());
    }

    private void onSelect(Observable<Location> selected) {
        eventsSub = selected.flatMap(location -> biletService.getEventsForLocation(location))
                .doOnNext(events1 -> Log.d(">>>>", "next"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> view.setEvents(events),
                        t -> {Log.d("!!!!!!!!!!!!!!!!!!!!!!!!", t.toString()); t.printStackTrace();});
    }



    @Override
    public void removeView() {
        eventsSub.unsubscribe();
        locationsSub.unsubscribe();
        //dragsWhileOfflineSub.unsubscribe();
        view = null;
    }
}
