package ro.asalajan.biletmaster.parser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.model.Venue;

public class BiletMasterParserImpl implements BiletMasterParser {

    public List<Event> parseEvents(InputStream inputStream) {
        try {
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");
            Elements stacktitles = doc.getElementsByClass("stacktitle"); //locations

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
            Elements _locations = doc.getElementsByClass("stacktitle");

            List<Location> locations = new ArrayList<>();

            for(Element _location : _locations) {
                Location loc = new Location(_location.text(), parseVenues(_location));
                locations.add(loc);
            }
            return locations;

        } catch (IOException e) {
            throw new RuntimeException("Parser failure", e);
        }
    }

    private List<Venue> parseVenues(Element location) {
        Element table = location.nextElementSibling();
        Elements _venue = table.getElementsByClass("title2");

        List<Venue> venues = new ArrayList<Venue>();

        for (Element _v : _venue) {
            Venue v = new Venue(_v.text(), _v.parent().attr("href"));
            venues.add(v);
        }
        return venues;
    }

}
