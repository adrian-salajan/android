package ro.asalajan.biletmaster.model;

import java.util.Objects;

public class Event {

    private String name;
    private String artist;

    public Event(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name) &&
                Objects.equals(artist, event.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
