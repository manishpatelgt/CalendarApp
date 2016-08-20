package me.vucko.calendarapp.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.vucko.calendarapp.alarm.Alarm;

public class CalendarDay {
    private String dayLetter;
    private Date dayDate;
    private List<CalendarEvent> calendarEvents = new ArrayList<>();
    private List<Alarm> calendarAlarms = new ArrayList<>();

    public CalendarDay(String dayLetter, Date dayDate, List<CalendarEvent> calendarEvents, List<Alarm> calendarAlarms) {
        this.dayLetter = dayLetter;
        this.dayDate = dayDate;
        this.calendarEvents = calendarEvents;
        this.calendarAlarms = calendarAlarms;
    }

    public String getDayLetter() {
        return dayLetter;
    }

    public void setDayLetter(String dayLetter) {
        this.dayLetter = dayLetter;
    }

    public Date getDayDate() {
        return dayDate;
    }

    public void setDayDate(Date dayDate) {
        this.dayDate = dayDate;
    }

    public List<CalendarEvent> getCalendarEvents() {
        return calendarEvents;
    }

    public void setCalendarEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public List<Alarm> getCalendarAlarms() {
        return calendarAlarms;
    }

    public void setCalendarAlarms(List<Alarm> calendarAlarms) {
        this.calendarAlarms = calendarAlarms;
    }
}
