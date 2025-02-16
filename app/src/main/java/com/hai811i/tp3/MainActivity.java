package com.hai811i.tp3;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventDeleteListener, EventAdapter.OnEventEditListener {


    private CalendarView calendarView;
    private ListView eventListView;
    private Button addEventButton;
    private Map<String, List<Event>> eventsMap;
    private EventAdapter eventAdapter;
    private String currentDate;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Button toggleEventsButton;
    private boolean areEventsVisible = false;
    private String[] colorNames = {"Rouge", "Vert", "Bleu", "Jaune", "Orange", "Violet"};
    private int[] colorValues = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFFA500, 0xFF800080}; // ARGB


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView horizontalCalendar = findViewById(R.id.horizontalCalendar);
        eventListView = findViewById(R.id.eventListView);
        addEventButton = findViewById(R.id.addEventButton);
        toggleEventsButton = findViewById(R.id.toggleEventsButton);


        sharedPreferences = getSharedPreferences("EventsData", MODE_PRIVATE);
        gson = new Gson();
        eventsMap = new HashMap<>();


        loadEvents();

        eventAdapter = new EventAdapter(this, new ArrayList<>(), this, this);
        eventListView.setAdapter(eventAdapter);

        // Configuration de la visibilité initiale
        eventListView.setVisibility(View.GONE);

        toggleEventsButton.setOnClickListener(v -> {
            areEventsVisible = !areEventsVisible;
            eventListView.setVisibility(areEventsVisible ? View.VISIBLE : View.GONE);
            toggleEventsButton.setText(areEventsVisible ?
                    "Masquer les événements" : "Afficher les événements");
            if (areEventsVisible) {
                updateEventList(currentDate);
            }
        });


        List<Date> dates = generateDates();
        HorizontalCalendarAdapter adapter = new HorizontalCalendarAdapter(this, dates, selectedDate -> {
            currentDate = selectedDate;
            if (areEventsVisible) {
                updateEventList(currentDate);
            }
        });
        horizontalCalendar.setAdapter(adapter);
        horizontalCalendar.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );


        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(today);
        adapter.selectDate(today);
        horizontalCalendar.scrollToPosition(adapter.getSelectedPosition());


        addEventButton.setOnClickListener(v -> showAddEventDialog());
    }


    private List<Date> generateDates() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = -15; i <= 15; i++) {
            Calendar temp = (Calendar) calendar.clone();
            temp.add(Calendar.DAY_OF_MONTH, i);
            dates.add(temp.getTime());
        }
        return dates;
    }


    private void loadEvents() {
        String eventsJson = sharedPreferences.getString("events", null);
        Type type = new TypeToken<HashMap<String, List<Event>>>(){}.getType();
        eventsMap = eventsJson != null ?
                gson.fromJson(eventsJson, type) :
                new HashMap<>();
    }


    private void saveEvents() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String eventsJson = gson.toJson(eventsMap);
        editor.putString("events", eventsJson);
        editor.apply();
    }

    @Override
    public void onEventDelete(Event event) {

        List<Event> events = eventsMap.get(currentDate);
        if (events != null) {
            events.remove(event);
            if (events.isEmpty()) {
                eventsMap.remove(currentDate);
            }

            saveEvents();
        }

        updateEventList(currentDate);
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateEventList(String date) {
        if (!areEventsVisible) return;

        sortEventsByTime(date);

        List<Event> events = eventsMap.get(date);
        eventAdapter.clear();
        if (events != null) {
            eventAdapter.addAll(events);
        }
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventEdit(Event event) {
        showEditEventDialog(event);
    }


    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        Button timePickerButton = dialogView.findViewById(R.id.timePickerButton);
        Spinner colorPicker = dialogView.findViewById(R.id.eventColorSpinner);
        Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);

        final AlertDialog dialog = builder.create();
        final String[] selectedTime = {""};


        setupColorSpinner(colorPicker);

        timePickerButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minuteOfDay) -> {
                        selectedTime[0] = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                        timePickerButton.setText("Heure sélectionnée : " + selectedTime[0]);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        saveEventButton.setOnClickListener(v -> {
            String title = eventTitleInput.getText().toString();
            int selectedColor = colorValues[colorPicker.getSelectedItemPosition()];

            if (!title.isEmpty() && !selectedTime[0].isEmpty()) {

                Event event = new Event(title, selectedTime[0], selectedColor);


                if (!eventsMap.containsKey(currentDate)) {
                    eventsMap.put(currentDate, new ArrayList<>());
                }
                eventsMap.get(currentDate).add(event);


                sortEventsByTime(currentDate);
                saveEvents();
                updateEventList(currentDate);

                Toast.makeText(MainActivity.this, "Événement ajouté", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this,
                        "Veuillez entrer un titre et sélectionner une heure",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


    private void showEditEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        Button timePickerButton = dialogView.findViewById(R.id.timePickerButton);
        Spinner colorPicker = dialogView.findViewById(R.id.eventColorSpinner);
        Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);


        eventTitleInput.setText(event.getTitle());
        timePickerButton.setText("Heure sélectionnée : " + event.getTime());
        setupColorSpinner(colorPicker);


        int selectedColorIndex = -1;
        for (int i = 0; i < colorValues.length; i++) {
            if (colorValues[i] == event.getColor()) {
                selectedColorIndex = i;
                break;
            }
        }
        colorPicker.setSelection(selectedColorIndex);

        final AlertDialog dialog = builder.create();
        final String[] selectedTime = {event.getTime()};

        timePickerButton.setOnClickListener(v -> {
            String[] timeParts = event.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minuteOfDay) -> {
                        selectedTime[0] = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                        timePickerButton.setText("Heure sélectionnée : " + selectedTime[0]);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        saveEventButton.setOnClickListener(v -> {
            String title = eventTitleInput.getText().toString();
            int selectedColor = colorValues[colorPicker.getSelectedItemPosition()];

            if (!title.isEmpty() && !selectedTime[0].isEmpty()) {

                List<Event> events = eventsMap.get(currentDate);
                if (events != null) {
                    events.remove(event);
                }


                Event updatedEvent = new Event(title, selectedTime[0], selectedColor);
                if (!eventsMap.containsKey(currentDate)) {
                    eventsMap.put(currentDate, new ArrayList<>());
                }
                eventsMap.get(currentDate).add(updatedEvent);


                saveEvents();
                updateEventList(currentDate);

                Toast.makeText(MainActivity.this, "Événement modifié", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this,
                        "Veuillez entrer un titre et sélectionner une heure",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setupColorSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                colorNames);
        spinner.setAdapter(adapter);
    }

    private void sortEventsByTime(String date) {
        List<Event> events = eventsMap.get(date);
        if (events != null) {
            events.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));
        }
    }

}