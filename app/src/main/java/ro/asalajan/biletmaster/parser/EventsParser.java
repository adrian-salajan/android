package ro.asalajan.biletmaster.parser;

import java.io.InputStream;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;

public interface EventsParser {
    List<Event> parseEvents(InputStream inputStream);
}
