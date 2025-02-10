package com.hai811i.tp3;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView eventListView;
    private Button addEventButton;
    private Map<String, List<Event>> eventsMap; // Map to store events by date
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        calendarView = findViewById(R.id.calendarView);
        eventListView = findViewById(R.id.eventListView);
        addEventButton = findViewById(R.id.addEventButton);

        // Initialize the events map
        eventsMap = new HashMap<>();

        // Set up the event adapter
        eventAdapter = new EventAdapter(this, new ArrayList<>());
        eventListView.setAdapter(eventAdapter);

        // Handle date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                updateEventList(selectedDate);
            }
        });

        // Handle add event button click
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog();
            }
        });
    }

    // Update the event list for the selected date
    private void updateEventList(String date) {
        List<Event> events = eventsMap.get(date);
        if (events == null) {
            events = new ArrayList<>();
        }
        eventAdapter.clear();
        eventAdapter.addAll(events);
    }

    // Show a dialog to add a new event
    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        EditText eventTimeInput = dialogView.findViewById(R.id.eventTimeInput);
        Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);

        AlertDialog dialog = builder.create();

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eventTitleInput.getText().toString();
                String time = eventTimeInput.getText().toString();

                if (!title.isEmpty() && !time.isEmpty()) {
                    // Get the selected date
                    long selectedDate = calendarView.getDate();
                    String date = android.text.format.DateFormat.format("dd/MM/yyyy", selectedDate).toString();

                    // Create a new event
                    Event event = new Event(title, time);

                    // Add the event to the map
                    if (!eventsMap.containsKey(date)) {
                        eventsMap.put(date, new ArrayList<>());
                    }
                    eventsMap.get(date).add(event);

                    // Update the event list
                    updateEventList(date);

                    // Close the dialog
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}