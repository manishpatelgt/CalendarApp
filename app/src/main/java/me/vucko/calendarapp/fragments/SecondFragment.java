package me.vucko.calendarapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.DetailedDayActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.adapters.DayEntryAdapter;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.domain.CalendarDay;
import me.vucko.calendarapp.domain.CalendarEvent;


public class SecondFragment extends Fragment {

    private static final long MILLISECONDS_IN_DAY = 60*60*24*1000;

    ListView dayEntryListView;
    DayEntryAdapter dayEntryAdapter;

    public static SecondFragment newInstance() {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        Date d = new Date();
        long x = d.getTime();
        java.util.Calendar calendarD = java.util.Calendar.getInstance();
        calendarD.setTime(d);
        Date d1 = new Date(x+ MILLISECONDS_IN_DAY);
        java.util.Calendar calendarD1 = java.util.Calendar.getInstance();
        calendarD1.setTime(d1);
        Date d2 = new Date(x+ MILLISECONDS_IN_DAY *2);
        java.util.Calendar calendarD2 = java.util.Calendar.getInstance();
        calendarD2.setTime(d2);
        Date d3 = new Date(x+ MILLISECONDS_IN_DAY *3);
        java.util.Calendar calendarD3 = java.util.Calendar.getInstance();
        calendarD3.setTime(d3);
        Date d4 = new Date(x+ MILLISECONDS_IN_DAY *4);
        java.util.Calendar calendarD4 = java.util.Calendar.getInstance();
        calendarD4.setTime(d4);
        Date d5 = new Date(x+ MILLISECONDS_IN_DAY *5);
        java.util.Calendar calendarD5 = java.util.Calendar.getInstance();
        calendarD5.setTime(d5);
        Date d6 = new Date(x+ MILLISECONDS_IN_DAY *6);
        java.util.Calendar calendarD6 = java.util.Calendar.getInstance();
        calendarD6.setTime(d6);

        Database.init(getContext());
        List<Alarm> alarms = Database.getAll();
        List<CalendarEvent> calendarEventsD = new ArrayList<>();
        List<CalendarEvent> calendarEventsD1 = new ArrayList<>();
        List<CalendarEvent> calendarEventsD2 = new ArrayList<>();
        List<CalendarEvent> calendarEventsD3 = new ArrayList<>();
        List<CalendarEvent> calendarEventsD4 = new ArrayList<>();
        List<CalendarEvent> calendarEventsD5 = new ArrayList<>();
        List<CalendarEvent> calendarEventsD6 = new ArrayList<>();

        List<Alarm> calendarAlarmsD = new ArrayList<>();
        List<Alarm> calendarAlarmsD1 = new ArrayList<>();
        List<Alarm> calendarAlarmsD2 = new ArrayList<>();
        List<Alarm> calendarAlarmsD3 = new ArrayList<>();
        List<Alarm> calendarAlarmsD4 = new ArrayList<>();
        List<Alarm> calendarAlarmsD5 = new ArrayList<>();
        List<Alarm> calendarAlarmsD6 = new ArrayList<>();

        for (int i = 0; i < alarms.size(); i++) {
            long eventMillis = alarms.get(i).getAlarmEventTime().getTimeInMillis();
            if (alarms.get(i).getEvent()) {
                CalendarEvent calendarEvent = new CalendarEvent(alarms.get(i).getAlarmName(), alarms.get(i).getAlarmEventTime());
                if ((d.getTime() < eventMillis) && (eventMillis < d1.getTime()))
                    calendarEventsD.add(calendarEvent);
                if ((d1.getTime() < eventMillis) && (eventMillis < d2.getTime()))
                    calendarEventsD1.add(calendarEvent);
                if ((d2.getTime() < eventMillis) && (eventMillis < d3.getTime()))
                    calendarEventsD2.add(calendarEvent);
                if ((d3.getTime() < eventMillis) && (eventMillis < d4.getTime()))
                    calendarEventsD3.add(calendarEvent);
                if ((d4.getTime() < eventMillis) && (eventMillis < d5.getTime()))
                    calendarEventsD4.add(calendarEvent);
                if ((d5.getTime() < eventMillis) && (eventMillis < d6.getTime()))
                    calendarEventsD5.add(calendarEvent);
                if ((d6.getTime() < eventMillis) && (eventMillis < d6.getTime() + MILLISECONDS_IN_DAY))
                    calendarEventsD6.add(calendarEvent);
            } else {
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD1.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD1.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD2.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD2.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD3.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD3.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD4.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD4.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD5.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD5.add(alarms.get(i));
                if (Arrays.asList(alarms.get(i).getDays()).contains(convert(calendarD6.get(java.util.Calendar.DAY_OF_WEEK))))
                    calendarAlarmsD6.add(alarms.get(i));
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E", Locale.getDefault());
        CalendarDay[] calendarDays = {
                new CalendarDay(simpleDateFormat.format(d).substring(0,1), d, calendarEventsD, calendarAlarmsD),
                new CalendarDay(simpleDateFormat.format(d1).substring(0,1), d1, calendarEventsD1, calendarAlarmsD1),
                new CalendarDay(simpleDateFormat.format(d2).substring(0,1), d2, calendarEventsD2, calendarAlarmsD2),
                new CalendarDay(simpleDateFormat.format(d3).substring(0,1), d3, calendarEventsD3, calendarAlarmsD3),
                new CalendarDay(simpleDateFormat.format(d4).substring(0,1), d4, calendarEventsD4, calendarAlarmsD4),
                new CalendarDay(simpleDateFormat.format(d5).substring(0,1), d5, calendarEventsD5, calendarAlarmsD5),
                new CalendarDay(simpleDateFormat.format(d6).substring(0,1), d6, calendarEventsD6, calendarAlarmsD6),
        };
        dayEntryAdapter = new DayEntryAdapter(context, R.layout.custom_day_entry, calendarDays);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        dayEntryListView =(ListView) view.findViewById(R.id.dayEntryListView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dayEntryListView.setAdapter(dayEntryAdapter);
        dayEntryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedDayActivity.class);
                intent.putExtra("position",position);
                getActivity().startActivity(intent);
            }
        });
    }

    private Alarm.Day convert(int day) {
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
}
