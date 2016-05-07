package ro.asalajan.biletmaster.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.model.Location;

public class LocationAdapter extends ArrayAdapter<Location> {
    public LocationAdapter(Context context, List<Location> locations) {
        super(context, 0, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Location loc = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.active_spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.active_spinner_item);

        textView.setText(loc.getLocation());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Location loc = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.location_view, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.locationName);

        textView.setText(loc.getLocation());

        return convertView;
    }
}
