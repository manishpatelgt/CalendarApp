package me.vucko.calendarapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import me.vucko.calendarapp.R;

public class EditEventAlarmDialog extends Dialog {
    public EditEventAlarmDialog(Context context) {
        super(context);
        setCancelable(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_event_alarm_dialog);

        Button okButton = (Button) findViewById(R.id.okButton);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
    }
}
