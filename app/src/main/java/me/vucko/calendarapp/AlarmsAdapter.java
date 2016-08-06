package me.vucko.calendarapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;

public class AlarmsAdapter extends BaseAdapter {

    private List<Alarm> alarms = new ArrayList<>();
    private Context context;

    public AlarmsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Alarm getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_alarm_entry, parent, false);
        }

        Alarm alarm = getItem(position);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String time = simpleDateFormat.format(alarm.getAlarmTime().getTime());
        timeTextView.setText(time);

        ImageButton xButton = (ImageButton) convertView.findViewById(R.id.x_button);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.remove(position);
                callAlarmScheduleService();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    protected void callAlarmScheduleService() {
        Intent AlarmServiceIntent = new Intent(context, AlarmServiceBroadcastReciever.class);
        context.sendBroadcast(AlarmServiceIntent, null);
    }
}
