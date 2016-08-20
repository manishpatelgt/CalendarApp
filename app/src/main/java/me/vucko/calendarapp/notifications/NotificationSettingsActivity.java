package me.vucko.calendarapp.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TimePicker;

import java.sql.Time;

import me.vucko.calendarapp.R;

public class NotificationSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TimePicker notificationTimePicked = (TimePicker) findViewById(R.id.notificationTimePicker);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (notificationTimePicked != null) {
            notificationTimePicked.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = notificationTimePicked.getCurrentHour() * 60 + notificationTimePicked.getCurrentMinute();
                    editor.putInt("notificationTimePicker", minutes);
                }
            });
        }

        final TimePicker eventsBeforeTimePicker = (TimePicker) findViewById(R.id.eventsBeforeTimePicker);

        if (eventsBeforeTimePicker != null) {
            eventsBeforeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = eventsBeforeTimePicker.getCurrentHour() * 60 + eventsBeforeTimePicker.getCurrentMinute();
                    editor.putInt("eventsBeforeTimePicker", minutes);
                }
            });
        }

        final TimePicker eventsBeforeFirstAlarmByTimePicker = (TimePicker) findViewById(R.id.eventsBeforeFirstAlarmByTimePicker);

        if (eventsBeforeFirstAlarmByTimePicker != null) {
            eventsBeforeFirstAlarmByTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = eventsBeforeFirstAlarmByTimePicker.getCurrentHour() * 60 + eventsBeforeFirstAlarmByTimePicker.getCurrentMinute();
                    editor.putInt("eventsBeforeFirstAlarmByTimePicker", minutes);
                }
            });
        }

        final TimePicker excludeAlarmsBeforeTimePicker = (TimePicker) findViewById(R.id.excludeAlarmsBeforeTimePicker);

        if (excludeAlarmsBeforeTimePicker != null) {
            excludeAlarmsBeforeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = excludeAlarmsBeforeTimePicker.getCurrentHour() * 60 + excludeAlarmsBeforeTimePicker.getCurrentMinute();
                    editor.putInt("excludeAlarmsBeforeTimePicker    ", minutes);
                }
            });
        }

        assert toolbar != null;
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
