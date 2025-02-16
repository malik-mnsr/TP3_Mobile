package com.hai811i.tp3;

import java.util.Objects;

public class Event {
    private String title;
    private String time;
    private int color;


    public Event(String title, String time, int color) {
        this.title = title;
        this.time = time;
        this.color = color;
    }


    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(title, event.title) &&
                Objects.equals(time, event.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
