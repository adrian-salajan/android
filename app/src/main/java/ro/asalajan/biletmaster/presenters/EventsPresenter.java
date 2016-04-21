package ro.asalajan.biletmaster.presenters;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.services.BiletMasterHelper;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.view.EventsView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class EventsPresenter implements Presenter<EventsView>  {


    private BiletMasterService biletService;
    private EventsView view;

    public EventsPresenter(BiletMasterService biletService) {
        this.biletService = biletService;
    }

    @Override
    public void setView(EventsView view) {

        this.view = view;

        biletService.getDistinctLocations(BiletMasterHelper.DISTINCT_LOCATIONS)
                .subscribe(view.locationsSubscriber());

        onSelect(view.getSelectedLocation());

    }

    private void onSelect(Observable<Location> selected) {
        selected.flatMap(location -> biletService.getEventsForLocation(location))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(view.eventsSubscriber());
    }

//    public void onSelect(Location location) {
//        biletService.getEventsForLocation(location)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(events -> view.setEvents(events),
//                        throwable -> System.out.println("<<<<<" + throwable.toString()));
//    }

    @Override
    public void removeView() {
        this.view = null;
    }
}
