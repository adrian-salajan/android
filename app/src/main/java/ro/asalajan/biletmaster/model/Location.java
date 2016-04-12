package ro.asalajan.biletmaster.model;

import java.util.Objects;

public class Location {

    private String location;

    public Location(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location1 = (Location) o;
        return Objects.equals(location, location1.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return "Location{" +
                "location='" + location + '\'' +
                '}';
    }
}
