package me.vucko.calendarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.alarm.Alarm;

public class AlarmsAdapter extends ArrayAdapter<Alarm> {

    private List<Alarm> alarms = new ArrayList<>();
    private int customLayoutId;
    private Context context;

    public AlarmsAdapter(Context context, int resource, List<Alarm> alarms) {
        super(context, resource, alarms);
        this.alarms = alarms;
        this.customLayoutId = resource;
        this.context = context;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(customLayoutId, parent, false);
            Alarm alarm = alarms.get(position);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            String time = simpleDateFormat.format(alarm.getAlarmTime().getTime());
            timeTextView.setText(time);
        }
        return convertView;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }
}
