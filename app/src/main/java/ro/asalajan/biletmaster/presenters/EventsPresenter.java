package ro.asalajan.biletmaster.presenters;

import android.util.Log;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.services.BiletMasterHelper;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.services.BiletMasterServiceImpl;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class EventsPresenter implements Presenter<EventsView>  {


    private BiletMasterService biletService;
    private EventsView view;
    private Subscription locationsSub;
    private Subscription eventsSub;

    public EventsPresenter(BiletMasterService biletService) {
        this.biletService = biletService;
    }

    @Override
    public void setView(EventsView view) {

        this.view = view;

        locationsSub = biletService.getDistinctLocations(BiletMasterHelper.DISTINCT_LOCATIONS)
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
        view = null;
    }
}
