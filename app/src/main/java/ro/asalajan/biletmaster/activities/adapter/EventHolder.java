package ro.asalajan.biletmaster.activities.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EventHolder {

    private int position;

    private View name;
    private View artist;
    private View venue;

    private View calendarIcon;

    public void setPosition(int position) {
        this.position = position;
    }

    public void setName(View name) {
        this.name = name;
    }

    public void setArtist(View artist) {
        this.artist = artist;
    }

    public void setVenue(View venue) {
        this.venue = venue;
    }

    public void setCalendarIcon(ImageView calendarIcon) {
        this.calendarIcon = calendarIcon;
    }

    public void updateName(String text) {
        if (name != null) {
            ((TextView)name).setText(text);
        }
    }

    public void updateArtist(String text) {
        if (artist != null) {
            ((TextView)artist).setText(text);
        }
    }

    public void updateVenue(String text) {
        if (venue != null) {
            ((TextView) venue).setText(text);
        }
    }
}
