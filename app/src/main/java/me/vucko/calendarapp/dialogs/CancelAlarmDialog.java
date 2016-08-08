package me.vucko.calendarapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import me.vucko.calendarapp.R;

public class CancelAlarmDialog extends Dialog {
    public CancelAlarmDialog(Context context) {
        super(context);
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_cancel_alarm_dialog);

        Button todayButton = (Button) findViewById(R.id.todayButton);
        Button allDayOfWeeksButton = (Button) findViewById(R.id.allDayOfWeeksButton);
        Button entireAlarmButton = (Button) findViewById(R.id.entireAlarmButton);
    }
}
