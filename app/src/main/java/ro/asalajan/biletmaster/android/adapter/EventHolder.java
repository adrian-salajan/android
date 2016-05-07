package ro.asalajan.biletmaster.android.adapter;

import android.view.View;
import android.widget.TextView;

public class EventHolder {

    private int position;

    private View name;
    private View artist;
    private View venue;

    private View dateDay;
    private View dateMonth;

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


    public void setDateDay(View dateDay) {
        this.dateDay = dateDay;
    }

    public void setDateMonth(View dateMonth) {
        this.dateMonth = dateMonth;
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

    public void updateDateDay(String text) {
        if (dateDay != null) {
            ((TextView) dateDay).setText(text);
        }
    }

    public void updateDateMonth(String text) {
        if (dateMonth != null) {
            ((TextView) dateMonth).setText(text);
        }
    }
}
