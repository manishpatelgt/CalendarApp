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

import java.util.Date;

import me.vucko.calendarapp.DetailedDayActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.adapters.DayEntryAdapter;
import me.vucko.calendarapp.domain.CalendarDay;


public class SecondFragment extends Fragment {

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
        CalendarDay[] calendarDays = {
                new CalendarDay("M", new Date(), null),
                new CalendarDay("T", new Date(), null),
                new CalendarDay("W", new Date(), null),
                new CalendarDay("T", new Date(), null),
                new CalendarDay("F", new Date(), null),
                new CalendarDay("S", new Date(), null),
                new CalendarDay("S", new Date(), null),
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
