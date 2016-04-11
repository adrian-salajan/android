package ro.asalajan.biletmaster.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;

public class EventsParserImpl implements EventsParser {

    public List<Event> parseEvents(InputStream inputStream) {
        try {
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");
            Elements stacktitles = doc.getElementsByClass("stacktitle"); //event locations

            List<Event> events = new ArrayList<>();

            for(Element stacktitle : stacktitles) {
                String location = stacktitle.text();

                Event e = new Event(location);
                events.add(e);
            }
            return events;

        } catch (IOException e) {
            throw new RuntimeException("Parser failure", e);
        }
    }

    @Override
    public List<Location> parseLocations(InputStream inputStream) {
        try {
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");
            Elements stacktitles = doc.getElementsByClass("stacktitle"); //event locations

            List<Location> events = new ArrayList<>();

            for(Element stacktitle : stacktitles) {
                String location = stacktitle.text();

                Location loc = new Location(location);
                events.add(loc);
            }
            return events;

        } catch (IOException e) {
            throw new RuntimeException("Parser failure", e);
        }
    }

}
