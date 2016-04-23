package ro.asalajan.biletmaster.activities.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Event;

public class EventAdapter extends ArrayAdapter<Event> {

    private static String name = "EventAdapter";
    private static final int LAYOUT_COUNT = 3;
    private static final int LAYOUT_FULL = 1;
    private static final int LAYOUT_NO_ARTIST = 2;
    private static final int LAYOUT_NO_ROOM = 3;


    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public int getViewTypeCount() {
        return LAYOUT_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Event event = getItem(position);
        if (event.getArtist() == null) {
            return LAYOUT_NO_ARTIST;
        }
        if (event.getRoom() == null) {
            return LAYOUT_NO_ROOM;
        }
        return LAYOUT_FULL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        if (convertView == null) {
            int layout = getItemViewType(position);
            convertView = getViewForLayout(layout, event, parent);
        } else {
            EventHolder holder = (EventHolder) convertView.getTag();
            holder.updateName(event.getName());
            holder.updateArtist(event.getArtist());
            holder.updateRoom(event.getRoom());
        }
        return convertView;
    }

    @NonNull
    private View getViewForLayout(int layout, Event event, ViewGroup parent ) {
        View view;
        EventHolder holder = new EventHolder();
        if (layout == LAYOUT_NO_ARTIST) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_view_no_artist, parent, false);

            holder.setRoom(init(R.id.eventRoom, event.getRoom(), view));
        } else if (layout == LAYOUT_NO_ROOM) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_view_no_room, parent, false);
            holder.setArtist(init(R.id.eventArtist, event.getArtist(), view));
        } else {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_view_all_fields, parent, false);

            holder.setArtist(init(R.id.eventArtist, event.getArtist(), view));
            holder.setRoom(init(R.id.eventRoom, event.getRoom(), view));
        }
        holder.setName(init(R.id.eventName, event.getName(), view));

        holder.setCalendarIcon((ImageView) view.findViewById(R.id.calendarIcon));

        view.setTag(holder);

        return view;
    }

    private View init(int id, String text, View view) {
        TextView element = (TextView) view.findViewById(id);
        element.setText(text);
        return element;
    }
}
