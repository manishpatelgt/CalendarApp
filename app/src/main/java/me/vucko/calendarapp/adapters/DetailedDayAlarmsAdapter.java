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
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.preferences.AlarmPreferencesActivity;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.dialogs.CancelAlarmDialog;
import me.vucko.calendarapp.dialogs.EditEventAlarmDialog;
import me.vucko.calendarapp.domain.eventbus_events.AlarmDeletedEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmEditedEvent;

public class DetailedDayAlarmsAdapter extends BaseAdapter {

    private List<Alarm> alarms = new ArrayList<>();
    private Context context;
    private Alarm.Day day;

    public DetailedDayAlarmsAdapter(Context context, List<Alarm> alarms, Alarm.Day day) {
        this.context = context;
        this.alarms = alarms;
        this.day = day;
        EventBus.getDefault().register(this);
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
            convertView = inflater.inflate(R.layout.custom_alarm_entry_in_detailed_day, parent, false);
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
        if (alarm.getEvent()) {
            alarmRepeatImageView.setImageResource(R.drawable.ic_event_black_24px);
        } else
        if (!alarm.isRepeating()) {
            alarmRepeatImageView.setVisibility(View.INVISIBLE);
        }

        ImageView editAlarmImageView = (ImageView) convertView.findViewById(R.id.editAlarmImageView);
        editAlarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditEventAlarmDialog editEventAlarmDialog = new EditEventAlarmDialog(context, alarm);
                editEventAlarmDialog.show();
            }
        });

        ImageButton xButton = (ImageButton) convertView.findViewById(R.id.x_button);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAlarmDialog cancelAlarmDialog = new CancelAlarmDialog(context, alarm, day);
                cancelAlarmDialog.show();
//                Database.init(context);
//                Database.deleteEntry(alarms.get(position));
//                alarms.remove(position);
//                callAlarmScheduleService();
//                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Subscribe
    public void onAlarmEditedEvent(AlarmEditedEvent alarmEditedEvent){
        Database.init(context);
        Database.update(alarmEditedEvent.getAlarm());
        alarms.remove(alarmEditedEvent.getAlarm());
        callAlarmScheduleService();
        notifyDataSetChanged();
    }

    public void onAlarmDeletedEvent(AlarmDeletedEvent alarmDeletedEvent){
        Database.init(context);
        Database.deleteEntry(alarmDeletedEvent.getAlarm());
        alarms.remove(alarmDeletedEvent.getAlarm());
        callAlarmScheduleService();
        notifyDataSetChanged();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    protected void callAlarmScheduleService() {
        Intent AlarmServiceIntent = new Intent(context, AlarmServiceBroadcastReciever.class);
        context.sendBroadcast(AlarmServiceIntent, null);
    }
}
