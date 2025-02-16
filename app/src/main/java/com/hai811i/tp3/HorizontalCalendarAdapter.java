package com.hai811i.tp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.DateViewHolder> {

    private final List<Date> dates;
    private final OnDateSelectedListener listener;
    private final Context context;
    private int selectedPosition = -1;
    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate);
    }

    public HorizontalCalendarAdapter(Context context, List<Date> dates, OnDateSelectedListener listener) {
        this.context = context;
        this.dates = dates;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Date date = dates.get(position);

        // Format the date into day, month, and year
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        String day = dayFormat.format(date);
        String month = monthFormat.format(date);
        String year = yearFormat.format(date);


        holder.dayTextView.setText(day);
        holder.monthTextView.setText(month);
        holder.yearTextView.setText(year);


        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.date_rectangle_selected_background);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.date_rectangle_background);
        }


        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
            listener.onDateSelected(
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            );
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    /**
     * Method to select today's date programmatically.
     *
     * @param today Today's date
     */
    public void selectDate(Date today) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayFormatted = dateFormat.format(today);


        for (int i = 0; i < dates.size(); i++) {
            String dateFormatted = dateFormat.format(dates.get(i));
            if (todayFormatted.equals(dateFormatted)) {
                selectedPosition = i;
                notifyDataSetChanged();
                break;
            }
        }
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView monthTextView;
        TextView yearTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;

    }
}
