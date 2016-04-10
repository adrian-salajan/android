package ro.asalajan.biletmaster.model;

/**
 * Created by adrian on 8/4/2016.
 */
public class Event {

    private String location;

    public Event(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
