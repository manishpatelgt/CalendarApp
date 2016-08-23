package me.vucko.calendarapp.domain.eventbus_events;

import me.vucko.calendarapp.alarm.Alarm;

public class AlarmEditedEvent {

    private Alarm alarm;

    public AlarmEditedEvent(Alarm alarm) {
        this.alarm = alarm;
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
