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
import java.util.Date;
import java.util.Locale;

import me.vucko.calendarapp.DetailedDayActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.adapters.DayEntryAdapter;
import me.vucko.calendarapp.domain.CalendarDay;


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
        Date d1 = new Date(x+ MILLISECONDS_IN_DAY);
        Date d2 = new Date(x+ MILLISECONDS_IN_DAY *2);
        Date d3 = new Date(x+ MILLISECONDS_IN_DAY *3);
        Date d4 = new Date(x+ MILLISECONDS_IN_DAY *4);
        Date d5 = new Date(x+ MILLISECONDS_IN_DAY *5);
        Date d6 = new Date(x+ MILLISECONDS_IN_DAY *6);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E", Locale.getDefault());
        CalendarDay[] calendarDays = {
                // TODO: Umesto null treba da se passuju Google Calendar eventovi za taj dan
                new CalendarDay(simpleDateFormat.format(d).substring(0,1), d, null),
                new CalendarDay(simpleDateFormat.format(d1).substring(0,1), d1, null),
                new CalendarDay(simpleDateFormat.format(d2).substring(0,1), d2, null),
                new CalendarDay(simpleDateFormat.format(d3).substring(0,1), d3, null),
                new CalendarDay(simpleDateFormat.format(d4).substring(0,1), d4, null),
                new CalendarDay(simpleDateFormat.format(d5).substring(0,1), d5, null),
                new CalendarDay(simpleDateFormat.format(d6).substring(0,1), d6, null),
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
}
