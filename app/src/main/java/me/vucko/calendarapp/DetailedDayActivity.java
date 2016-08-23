package me.vucko.calendarapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.adapters.DetailedDayAlarmsAdapter;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.domain.eventbus_events.AlarmEditedEvent;

public class DetailedDayActivity extends AppCompatActivity {

    private DetailedDayAlarmsAdapter detailedDayAlarmsAdapter;
    private static final long MILLISECONDS_IN_DAY = 60*60*24*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 1);

        assert toolbar != null;
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ListView alarmsListView = (ListView) findViewById(R.id.alarmsListView);
        detailedDayAlarmsAdapter = new DetailedDayAlarmsAdapter(this, null);
        assert alarmsListView != null;
        Database.init(this);
        final List<Alarm> alarms = Database.getAll();
        List<Alarm> alarmList = new ArrayList<>();

        TextView customDateTime = (TextView) findViewById(R.id.customDateTime);
        TextView event1 = (TextView) findViewById(R.id.eventDetail1);
        TextView event2 = (TextView) findViewById(R.id.eventDetail2);
        TextView event3 = (TextView) findViewById(R.id.eventDetail3);

        Calendar calendar = Calendar.getInstance();

        Date d = new Date();
        long x = d.getTime();
        Date start = new Date(x+ MILLISECONDS_IN_DAY * position);
        Date end = new Date(x+ MILLISECONDS_IN_DAY * (position + 1));

        if (customDateTime != null) {
            SimpleDateFormat customDateFormat = new SimpleDateFormat("EEEEEEE, MMMM dd", Locale.getDefault());
            customDateTime.setText(customDateFormat.format(start));
        }

        int j = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());

        for (int i = 0; i < alarms.size(); i++) {
            long eventMillis = alarms.get(i).getAlarmEventTime().getTimeInMillis();
            if (alarms.get(i).getEvent()) {
                if ((start.getTime() < eventMillis) && (eventMillis < end.getTime())) {
                    j++;
                    if ((j == 1) && (event1 != null)) {
                        event1.setText(alarms.get(i).getAlarmName() + ' ' + simpleDateFormat.format(alarms.get(i).getAlarmEventTime().getTime()));

                    }

                    if ((j == 2) && (event2 != null)) {
                        event2.setText(alarms.get(i).getAlarmName() + ' ' + simpleDateFormat.format(alarms.get(i).getAlarmEventTime().getTime()));

                    }

                    if ((j == 3) && (event3 != null)) {
                        event3.setText(alarms.get(i).getAlarmName() + ' ' + simpleDateFormat.format(alarms.get(i).getAlarmEventTime().getTime()));
                    }
                }
            }
        }

        for (int i = 0; i < alarms.size(); i++) {
            if ((!alarms.get(i).getEvent()) && (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendar.get(Calendar.DAY_OF_WEEK) + position))))
                alarmList.add(alarms.get(i));
        }

        detailedDayAlarmsAdapter.setAlarms(alarmList);
        alarmsListView.setAdapter(detailedDayAlarmsAdapter);
        alarmsListView.setEmptyView(findViewById(R.id.emptyListView));
    }

    private Alarm.Day convert(int day) {
        day = day % 7;
        switch (day) {
            case 1 : return Alarm.Day.SUNDAY;
            case 2 : return Alarm.Day.MONDAY;
            case 3 : return Alarm.Day.TUESDAY;
            case 4 : return Alarm.Day.MONDAY;
            case 5 : return Alarm.Day.THURSDAY;
            case 6 : return Alarm.Day.FRIDAY;
            default : return Alarm.Day.SATURDAY;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onAlarmEditedEvent(AlarmEditedEvent alarmEditedEvent){
        detailedDayAlarmsAdapter.notifyDataSetChanged();
    }
}
