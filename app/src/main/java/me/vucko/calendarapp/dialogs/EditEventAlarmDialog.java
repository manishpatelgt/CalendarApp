package me.vucko.calendarapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import org.greenrobot.eventbus.EventBus;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.domain.eventbus_events.AlarmEditedEvent;

public class EditEventAlarmDialog extends Dialog {

    private Alarm alarm;

    public EditEventAlarmDialog(Context context, Alarm alarm) {
        super(context);
        this.alarm = alarm;
        setCancelable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_event_alarm_dialog);

        Button okButton = (Button) findViewById(R.id.okButton);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        timePicker.setCurrentHour(alarm.getAlarmTime().getTime().getHours());
        timePicker.setCurrentMinute((alarm.getAlarmTime().getTime().getMinutes()));

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                alarm.setAlarmTime(hour + ":" + minute);
                EventBus.getDefault().post(new AlarmEditedEvent(alarm));
                dismiss();
            }
        });
    }
}
