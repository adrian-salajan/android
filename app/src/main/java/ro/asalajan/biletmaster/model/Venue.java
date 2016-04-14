package ro.asalajan.biletmaster.model;

import java.util.Objects;

public class Venue {

    private String name;
    private String url;

    public Venue(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(name, venue.name) &&
                Objects.equals(url, venue.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }

    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
