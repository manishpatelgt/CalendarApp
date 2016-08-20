package me.vucko.calendarapp.domain;

import java.util.Calendar;

public class CalendarEvent {
    private String name;
    private Calendar calendar;

    public CalendarEvent(String name, Calendar calendar) {
        this.name = name;
        this.calendar = calendar;
    }

    public String getName() {
        return name;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
