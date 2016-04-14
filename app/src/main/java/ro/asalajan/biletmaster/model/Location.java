package ro.asalajan.biletmaster.model;

import java.util.List;
import java.util.Objects;

public class Location {

    private String location;
    private List<Venue> venues;

    public Location(String location, List<Venue> venues) {
        this.location = location;
        this.venues = venues;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location1 = (Location) o;
        return Objects.equals(location, location1.location) &&
                Objects.equals(venues, location1.venues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, venues);
    }

    @Override
    public String toString() {
        return "Location{" +
                "location='" + location + '\'' +
                ", venues=" + venues +
                '}';
    }
}
