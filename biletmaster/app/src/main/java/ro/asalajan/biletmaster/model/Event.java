package ro.asalajan.biletmaster.model;

import com.google.common.base.Optional;

import org.joda.time.LocalDateTime;

import java.util.Objects;

public class Event {

    private String name;
    private String artist;
    private String room;
    private Optional<LocalDateTime> dateTime;
    private Venue venue;

    private boolean ticketsAvailable;
    private String url;

    public Event(String name, String artist, String room, Optional<LocalDateTime> dateTime, boolean ticketsAvailable, String url,  Venue venue) {
        this(name, artist, room, dateTime, ticketsAvailable, url);
        this.venue = venue;
    }

    public Event(String name, String artist, String room, Optional<LocalDateTime> dateTime,
                 boolean ticketsAvailable, String url) {
        this.name = name;
        this.artist = artist;
        this.room = room;
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

    public String getRoom() {
        return room;
    }

    public Optional<LocalDateTime> getDateTime() {
        return dateTime;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) &&
                Objects.equals(artist, event.artist) &&
                Objects.equals(room, event.room) &&
                Objects.equals(dateTime, event.dateTime) &&
                Objects.equals(venue, event.venue);

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, room, dateTime, venue);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", room='" + room + '\'' +
                ", venue=" + venue +
                ", dateTime=" + dateTime +
                '}';
    }
}
