package com.hai811i.tp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
public class EventAdapter extends ArrayAdapter<Event> {
    private final OnEventDeleteListener deleteListener;
    private final OnEventEditListener editListener;

    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    public interface OnEventEditListener {
        void onEventEdit(Event event);
    }

    public EventAdapter(Context context, List<Event> events,
                        OnEventDeleteListener deleteListener,
                        OnEventEditListener editListener) {
        super(context, 0, events);
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.event_item, parent, false);
        }


        TextView eventTitle = convertView.findViewById(R.id.eventTitle);
        TextView eventTime = convertView.findViewById(R.id.eventTime);
        View colorIndicator = convertView.findViewById(R.id.eventColorIndicator);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        ImageButton editButton = convertView.findViewById(R.id.editButton);


        eventTitle.setText(event.getTitle());
        eventTime.setText(event.getTime());
        colorIndicator.setBackgroundColor(event.getColor());


        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onEventDelete(event);
            }
        });

        editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEventEdit(event);
            }
        });

        return convertView;
    }
}