package me.vucko.calendarapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.domain.eventbus_events.AlarmDeletedEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmEditedEvent;

public class CancelAlarmDialog extends Dialog {

    private Alarm alarm;

    public CancelAlarmDialog(Context context, Alarm alarm) {
        super(context);
        this.alarm = alarm;
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
                Calendar calendar = Calendar.getInstance();
                Alarm.Day thisDay = Alarm.Day.values()[calendar.getTime().getDay()];
                alarm.removeDay(thisDay);
                EventBus.getDefault().post(new AlarmEditedEvent(alarm));
                dismiss();
            }
        });

        entireAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AlarmDeletedEvent(alarm));
                dismiss();
            }
        });
    }
}
