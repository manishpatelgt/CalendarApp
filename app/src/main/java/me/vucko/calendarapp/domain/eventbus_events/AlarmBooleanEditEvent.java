package me.vucko.calendarapp.domain.eventbus_events;

import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.preferences.AlarmPreference;

public class AlarmBooleanEditEvent {

    private Boolean isChecked;
    private AlarmPreference.Key key;

    public AlarmBooleanEditEvent(Boolean isChecked, AlarmPreference.Key key) {
        this.isChecked = isChecked;
        this.key = key;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public AlarmPreference.Key getKey() {
        return key;
    }
}