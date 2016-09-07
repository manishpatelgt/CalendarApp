package me.vucko.calendarapp.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.Calendar;
import java.util.Date;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.alert.AlarmAlertBroadcastReciever;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static final int MILLISECONDS_IN_DAY = 60*60*24*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TimePicker notificationTimePicked = (TimePicker) findViewById(R.id.notificationTimePicker);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (notificationTimePicked != null) {
            notificationTimePicked.setCurrentHour(sharedPreferences.getInt("notificationTimePicker", 0) / 60);
            notificationTimePicked.setCurrentMinute(sharedPreferences.getInt("notificationTimePicker", 0) % 60);
            notificationTimePicked.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = notificationTimePicked.getCurrentHour() * 60 + notificationTimePicked.getCurrentMinute();
                    editor.putInt("notificationTimePicker", minutes);
                    editor.apply();

                    Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReciever.class);
                    myIntent.putExtra("nijeAlarm", true);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                    alarmManager.cancel(pendingIntent);

                    Date d = new Date();
                    Calendar today = Calendar.getInstance();
                    today.setTime(d);
                    today.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("notificationTimePicker", 0) / 60);
                    today.set(Calendar.MINUTE, sharedPreferences.getInt("notificationTimePicker", 0) % 60);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);
                    Calendar now = Calendar.getInstance();
                    if ((now.get(Calendar.HOUR_OF_DAY) > hourOfDay) || ((now.get(Calendar.HOUR_OF_DAY) == hourOfDay) && (now.get(Calendar.MINUTE) > minute))) {
                        today.add(Calendar.MILLISECOND, MILLISECONDS_IN_DAY);
                    }

                    myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReciever.class);
                    myIntent.putExtra("nijeAlarm", true);

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

                    alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pendingIntent);
                }
            });
        }

        CheckBox eventsBeforeCheckbox = (CheckBox) findViewById(R.id.eventsBeforeCheckbox);

        if (eventsBeforeCheckbox != null) {
            eventsBeforeCheckbox.setChecked(sharedPreferences.getBoolean("eventsBeforeCheckbox", false));
            eventsBeforeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("eventsBeforeCheckbox", isChecked);
                    editor.apply();
                }
            });
        }

        final TimePicker eventsBeforeTimePicker = (TimePicker) findViewById(R.id.eventsBeforeTimePicker);

        if (eventsBeforeTimePicker != null) {
            eventsBeforeTimePicker.setCurrentHour(sharedPreferences.getInt("eventsBeforeTimePicker", 0) / 60);
            eventsBeforeTimePicker.setCurrentMinute(sharedPreferences.getInt("eventsBeforeTimePicker", 0) % 60);
            eventsBeforeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = eventsBeforeTimePicker.getCurrentHour() * 60 + eventsBeforeTimePicker.getCurrentMinute();
                    editor.putInt("eventsBeforeTimePicker", minutes);
                    editor.apply();
                }
            });
        }

        CheckBox eventsBeforeFirstAlarmByCheckbox = (CheckBox) findViewById(R.id.eventsBeforeFirstAlarmByCheckbox);
        if (eventsBeforeFirstAlarmByCheckbox != null) {
            eventsBeforeFirstAlarmByCheckbox.setChecked(sharedPreferences.getBoolean("eventsBeforeFirstAlarmByCheckbox", false));
            eventsBeforeFirstAlarmByCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("eventsBeforeFirstAlarmByCheckbox", isChecked);
                    editor.apply();
                }
            });
        }

        final NumberPicker eventsBeforeFirstAlarmByTimePicker = (NumberPicker) findViewById(R.id.eventsBeforeFirstAlarmByNumberPicker);

        if (eventsBeforeFirstAlarmByTimePicker != null) {
            eventsBeforeFirstAlarmByTimePicker.setValue(sharedPreferences.getInt("eventsBeforeFirstAlarmByTimePicker", 0));
            eventsBeforeFirstAlarmByTimePicker.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                    editor.putInt("eventsBeforeFirstAlarmByTimePicker", newVal);
                    editor.apply();
                }
            });
//            eventsBeforeFirstAlarmByTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                @Override
//                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                    int minutes = eventsBeforeFirstAlarmByTimePicker.getCurrentHour() * 60 + eventsBeforeFirstAlarmByTimePicker.getCurrentMinute();
//                    editor.putInt("eventsBeforeFirstAlarmByTimePicker", minutes);
//                }
//            });
        }

        CheckBox excludeAlarmsBeforeCheckbox = (CheckBox) findViewById(R.id.excludeAlarmsBeforeCheckbox);
        if (excludeAlarmsBeforeCheckbox != null) {
            excludeAlarmsBeforeCheckbox.setChecked(sharedPreferences.getBoolean("excludeAlarmsBeforeCheckbox", false));
            excludeAlarmsBeforeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("excludeAlarmsBeforeCheckbox", isChecked);
                    editor.apply();
                }
            });
        }

        final TimePicker excludeAlarmsBeforeTimePicker = (TimePicker) findViewById(R.id.excludeAlarmsBeforeTimePicker);

        if (excludeAlarmsBeforeTimePicker != null) {
            excludeAlarmsBeforeTimePicker.setCurrentHour(sharedPreferences.getInt("excludeAlarmsBeforeTimePicker", 0) / 60);
            excludeAlarmsBeforeTimePicker.setCurrentMinute(sharedPreferences.getInt("excludeAlarmsBeforeTimePicker", 0) % 60);
            excludeAlarmsBeforeTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int minutes = excludeAlarmsBeforeTimePicker.getCurrentHour() * 60 + excludeAlarmsBeforeTimePicker.getCurrentMinute();
                    editor.putInt("excludeAlarmsBeforeTimePicker", minutes);
                    editor.apply();
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
