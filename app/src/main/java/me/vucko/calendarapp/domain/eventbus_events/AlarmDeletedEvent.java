package me.vucko.calendarapp.domain.eventbus_events;

import me.vucko.calendarapp.alarm.Alarm;

public class AlarmDeletedEvent {

    private Alarm alarm;

    public AlarmDeletedEvent(Alarm alarm) {
        this.alarm = alarm;
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
