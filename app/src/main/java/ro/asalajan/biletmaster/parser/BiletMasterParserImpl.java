package ro.asalajan.biletmaster.parser;

import com.google.common.base.Optional;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import ro.asalajan.biletmaster.model.Venue;

public class BiletMasterParserImpl implements BiletMasterParser {

    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    public static final DateTimeFormatter FMT = DateTimeFormat.forPattern(DATE_FORMAT);

    public List<Event> parseEvents(InputStream inputStream) {
        try {
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");
            Elements candidateContainers = doc.getElementsByClass("elistlink");

            List<Event> events = new ArrayList<>();

            for(Element container : candidateContainers) {
                Event event = parseEvent(container);
                if (event != null) {
                    events.add(event);
                }
            }
            return events;

        } catch (IOException e) {
            throw new RuntimeException("Parser failure", e);
        }
    }

    private Event parseEvent(Element eventContainer) {
        Elements _event = eventContainer.getElementsByClass("title");
        Elements _artist = eventContainer.getElementsByClass("artist");
        if (_event.isEmpty()) {
            return null;
        }
        Element dateContainer = getDateContainer(eventContainer);
        //not sure if year imporant yet
        Optional<LocalDateTime> date = parseDate(dateContainer, DateUtils.getCurrentYear());
        // TODO parse tickets availability & url
        return new Event(_event.text(), _artist.text(), date, false, "url");

    }

    private Element getDateContainer(Element eventContainer) {
        Element td = eventContainer.parent();
        Element previousTd = td.previousElementSibling();
        return previousTd;
    }

    private Optional<LocalDateTime> parseDate(Element dateContainer, int year) {
        Element datePart = dateContainer.getElementsByClass("ajanlo-date").first();

        Element _month = getMonth(datePart);
        Element _day = getDay(datePart);

        Element timePart = dateContainer.getElementsByClass("dateplate").first();
        Element _time = getTime(timePart);

        if (_month == null || _day == null || _time == null) {
            return Optional.absent();
        }

        String newDate = new String(DATE_FORMAT);
        newDate = newDate.replace("dd", _day.text());
        newDate = newDate.replace("MM", parseMonth(_month.text()));
        newDate = newDate.replace("yyyy", String.valueOf(year));
        newDate = newDate.replace("HH:mm", _time.text());
        return Optional.fromNullable(FMT.parseLocalDateTime(newDate));

    }

    private Element getTime(Element timePart) {
        Element timeDiv = timePart.getElementsByClass("dateplate").first();
        if (timeDiv == null || timeDiv.children().isEmpty()) {
            return null;
        }
        return timeDiv.child(0);
    }

    private Element getMonth(Element datePart) {
        return datePart.getElementsByClass("ajanlo-date-top").first();
    }

    private Element getDay(Element datePart) {
        Element dayDiv = datePart.getElementsByClass("ajanlo-date-bottom2").first();
        if (dayDiv == null || dayDiv.children().isEmpty()) {
            return null;
        }
        return dayDiv.child(0);
    }

    private String parseMonth(String monthName) {
        switch (monthName) {
            case "IAN": return "01";
            case "FEB": return "02";
            case "MAR": return "03";
            case "APR": return "04";
            case "MAI": return "05";
            case "IUN": return "06";
            case "IUL": return "07";
            case "AUG": return "08";
            case "SEP": return "09";
            case "OCT": return "10";
            case "NOI": return "11";
            case "DEC": return "12";
            default: throw new IllegalStateException("Cannot parse month: " + monthName);
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
