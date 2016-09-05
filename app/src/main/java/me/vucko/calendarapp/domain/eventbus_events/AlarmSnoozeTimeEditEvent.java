package me.vucko.calendarapp.domain.eventbus_events;

import me.vucko.calendarapp.alarm.preferences.AlarmPreference;

public class AlarmSnoozeTimeEditEvent {

    private int time;
    private AlarmPreference.Key key;

    public AlarmSnoozeTimeEditEvent(int time, AlarmPreference.Key key) {
        this.time = time;
        this.key = key;
    }

    public int getTime() {
        return time;
    }

    public AlarmPreference.Key getKey() {
        return key;
    }
}
