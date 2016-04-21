package ro.asalajan.biletmaster.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import ro.asalajan.biletmaster.activities.RxUtils;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.BiletMasterParserImpl;
import ro.asalajan.biletmaster.services.BiletMasterHelper;
import ro.asalajan.biletmaster.services.BiletMasterService;
import ro.asalajan.biletmaster.services.HttpGateway;
import rx.Observer;
import rx.Subscription;
import rx.observers.Observers;

public class EventsActivityPresenter {

    private boolean isVisible = true;
    private BiletMasterService biletService = new BiletMasterService(new BiletMasterParserImpl(), new HttpGateway());

    private final Subscription locationsSub;

    public EventsActivityPresenter(UpdateLocationsPort updateLocationsPort) {

        locationsSub = biletService.getDistinctLocations(BiletMasterHelper.DISTINCT_LOCATIONS)
                .compose(RxUtils.ui())
                .subscribe(getLocationsObserver(updateLocationsPort));
    }

    @NonNull
    public Observer<List<Location>> getLocationsObserver(UpdateLocationsPort updateLocationsPort) {
        return Observers.create(
                locations -> {
                    if (isVisible) updateLocationsPort.doUpdateLocations(locations);
                },
                throwable -> {
                    if (isVisible) updateLocationsPort.handleError(throwable);
                }
        );
    }

    public void setVisibility(boolean isVisible) {
        Log.d("EventsPresenter", "isVisible = " + isVisible);
        this.isVisible = isVisible;
    }

    public void destroy() {
        locationsSub.unsubscribe();
        Log.d("EventsPresenter", "destroyed");
    }
}
