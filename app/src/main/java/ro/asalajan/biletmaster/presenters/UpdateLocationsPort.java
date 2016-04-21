package ro.asalajan.biletmaster.presenters;

import java.util.List;

import ro.asalajan.biletmaster.model.Location;

public interface UpdateLocationsPort {

    void doUpdateLocations(List<Location> locations);

    void handleError(Throwable throwable);
}
