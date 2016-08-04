package me.vucko.calendarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;

public class DetailedDayAlarmsAdapter extends ArrayAdapter<Alarm>{

    private Context context;
    private List<Alarm> alarms;
    LayoutInflater layoutInflater;


    public DetailedDayAlarmsAdapter(Context context, int resource, List<Alarm> objects) {
        super(context, resource, objects);
        this.context = context;
        this.alarms = objects;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.custom_day_entry, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayLetterTextView = (TextView) convertView.findViewById(R.id.dayLetterTextView);
            viewHolder.dayDateTextView = (TextView) convertView.findViewById(R.id.dayDateTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Alarm alarm = alarms.get(position);
        viewHolder.dayLetterTextView.setText(alarm.getAlarmTimeString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        viewHolder.dayDateTextView.setText("asdf");

        return convertView;
    }

    private static class ViewHolder {
        TextView dayLetterTextView;
        TextView dayDateTextView;
    }
}
