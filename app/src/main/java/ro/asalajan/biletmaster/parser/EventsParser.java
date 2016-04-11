package ro.asalajan.biletmaster.parser;

import java.io.InputStream;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;

public interface EventsParser {
    List<Event> parseEvents(InputStream inputStream);
    List<Location> parseLocations(InputStream inputStream);
}
