package ro.asalajan.biletmaster.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Event;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.event_view, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.eventName);
        TextView artist = (TextView) convertView.findViewById(R.id.eventArtist);

        name.setText(event.getName());
        artist.setText("Artist: " + event.getArtist());

        return convertView;
    }
}
