package me.vucko.calendarapp.domain.eventbus_events;

import me.vucko.calendarapp.alarm.preferences.AlarmPreference;

public class AlarmVolumeEditEvent {

    private int volume;
    private AlarmPreference.Key key;

    public AlarmVolumeEditEvent(int volume, AlarmPreference.Key key) {
        this.volume = volume;
        this.key = key;
    }

    public int getVolume() {
        return volume;
    }

    public AlarmPreference.Key getKey() {
        return key;
    }
}