package ro.asalajan.biletmaster.model;

import com.google.common.base.Optional;

import org.joda.time.LocalDateTime;

import java.util.Objects;

public class Event {

    private String name;
    private String artist;
    private Optional<LocalDateTime> dateTime;

    private boolean ticketsAvailable;
    private String url;

    public Event(String name, String artist, Optional<LocalDateTime> dateTime,
                 boolean ticketsAvailable, String url) {
        this.name = name;
        this.artist = artist;
        this.dateTime = dateTime;
        this.ticketsAvailable = ticketsAvailable;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Optional<LocalDateTime> getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) &&
                Objects.equals(artist, event.artist) &&
                Objects.equals(dateTime, event.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, dateTime);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", dateTime=" + dateTime +
                ", ticketsAvailable=" + ticketsAvailable +
                ", url='" + url + '\'' +
                '}';
    }
}
