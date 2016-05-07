package ro.asalajan.biletmaster.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.common.base.Optional;

import org.joda.time.LocalDateTime;

import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Event;

public class EventAdapter extends ArrayAdapter<Event> {

    private static String name = "EventAdapter";
    private static final int LAYOUT_COUNT = 2;
    private static final int LAYOUT_FULL = 0;
    private static final int LAYOUT_NO_ARTIST = 1;


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
            holder.updateVenue(event.getVenue().getName());
            holder.updateDateDay(getDay(event.getDateTime()));
            holder.updateDateMonth(getMonth(event.getDateTime()));
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
        } else {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_view_all_fields, parent, false);

            holder.setArtist(init(R.id.eventArtist, event.getArtist(), view));
        }
        holder.setName(init(R.id.eventName, event.getName(), view));
        holder.setVenue(init(R.id.eventVenue, event.getVenue().getName(), view));

        System.out.println("<<<<<<<<< event" + event);
        holder.setDateDay(init(R.id.date_day, getDay(event.getDateTime()), view));
        holder.setDateMonth(init(R.id.date_month, getMonth(event.getDateTime()), view));
        //holder.setCalendarIcon((View) view.findViewById(R.id.calendarIcon));

        view.setTag(holder);

        return view;
    }

    private String getDay(Optional<LocalDateTime> dateTime) {
        return dateTime.transform(dt -> String.valueOf(dt.getDayOfMonth())).or("?");
    }

    private String getMonth(Optional<LocalDateTime> dateTime) {
        return dateTime.transform(dt -> dt.getMonthOfYear())
                .transform(m -> monthToText(m))
                .or("???");
    }

    private String monthToText(int m) {
        switch (m) {
            case 0: return "Ianuarie";
            case 1: return "Februarie";
            case 2: return "Martie";
            case 3: return "Aprilie";
            case 4: return "Mai";
            case 5: return "Iunie";
            case 6: return "Iulie";
            case 7: return "August";
            case 8: return "Septembrie";
            case 9: return "Octombrie";
            case 10: return "Noiembrie";
            case 11: return "Decembrie";
            default: return "???";
        }
    }


    private View init(int id, String text, View view) {
        TextView element = (TextView) view.findViewById(id);
        element.setText(text);
        return element;
    }
}
