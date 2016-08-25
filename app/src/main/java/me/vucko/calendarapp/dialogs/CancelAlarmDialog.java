package me.vucko.calendarapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmDeletedEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmEditedEvent;

public class CancelAlarmDialog extends Dialog {

    private Alarm alarm;
    private Alarm.Day day;

    public CancelAlarmDialog(Context context, Alarm alarm, Alarm.Day day) {
        super(context);
        this.alarm = alarm;
        this.day = day;
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_cancel_alarm_dialog);

        Button todayButton = (Button) findViewById(R.id.todayButton);
        Button entireAlarmButton = (Button) findViewById(R.id.entireAlarmButton);

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.init(getContext());
                alarm.removeDay(day);
                Database.update(alarm);
                EventBus.getDefault().post(new AlarmEditedEvent(alarm));
                EventBus.getDefault().post(new AlarmChangeEvent());
                callAlarmScheduleService();
                dismiss();
            }
        });

        entireAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.init(getContext());
                Database.deleteEntry(alarm);
                EventBus.getDefault().post(new AlarmDeletedEvent(alarm));
                EventBus.getDefault().post(new AlarmChangeEvent());
                callAlarmScheduleService();
                dismiss();
            }
        });
    }

    protected void callAlarmScheduleService() {
        Intent AlarmServiceIntent = new Intent(getContext(), AlarmServiceBroadcastReciever.class);
        getContext().sendBroadcast(AlarmServiceIntent, null);
    }
}
