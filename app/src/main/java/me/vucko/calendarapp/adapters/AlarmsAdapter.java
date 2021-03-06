package me.vucko.calendarapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.preferences.AlarmPreferencesActivity;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;

public class AlarmsAdapter extends BaseAdapter {

    private List<Alarm> alarms = new ArrayList<>();
    private Context context;

    public AlarmsAdapter(Context context, List<Alarm> alarms) {
        this.context = context;
        this.alarms = alarms;
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

        final Alarm alarm = getItem(position);
        LinearLayout linearLayoutDayEntry = (LinearLayout) convertView.findViewById(R.id.linearLayoutDayEntry);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String time = simpleDateFormat.format(alarm.getAlarmTime().getTime());
        timeTextView.setText(time);

        linearLayoutDayEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlarmPreferencesActivity.class);
                intent.putExtra("alarm", alarm);
                context.startActivity(intent);
            }
        });

        ImageView alarmRepeatImageView = (ImageView) convertView.findViewById(R.id.alarmRepeatImageView);
        if (!alarm.isRepeating()){
            alarmRepeatImageView.setVisibility(View.INVISIBLE);
        }

        ImageButton xButton = (ImageButton) convertView.findViewById(R.id.x_button);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.init(context);
                Database.deleteEntry(alarms.get(position));
                alarms.remove(position);
                callAlarmScheduleService();
                notifyDataSetChanged();
                EventBus.getDefault().post(new AlarmChangeEvent());
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
