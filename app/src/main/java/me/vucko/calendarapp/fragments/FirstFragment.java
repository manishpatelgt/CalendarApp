package me.vucko.calendarapp.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import me.vucko.calendarapp.MainActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.adapters.AlarmsAdapter;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.preferences.AlarmPreferencesActivity;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;

public class FirstFragment extends Fragment {

    AlarmsAdapter alarmsAdapter;
    TextView emptyTextView;

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        Calendar.getInstance();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        Database.init(context);
        Database.getDatabase();
        final List<Alarm> alarms = Database.getAll();
        int size = alarms.size();
        for (int i = size - 1; i >= 0; i--) {
            if (alarms.get(i).getEvent()) {
                alarms.remove(i);
            }
        }
        alarmsAdapter = new AlarmsAdapter(context, alarms);
        alarmsAdapter.notifyDataSetChanged();
        super.onAttach(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateAlarmList();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        Button setButton = (Button) view.findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = new Alarm();
                alarm.setAlarmTime(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                Database.init(getActivity());
                Database.create(alarm);
                callAlarmScheduleService();
                updateAlarmList();
            }
        });
        Button moreSettingsButton = (Button) view.findViewById(R.id.moreSettingsButton);
        moreSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmPreferencesActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ListView alarmListView = (ListView) getView().findViewById(R.id.alarms_listview);

        alarmListView.setAdapter(alarmsAdapter);
        emptyTextView = (TextView) getView().findViewById(android.R.id.empty);
        if(alarmsAdapter.getCount() > 0){
            emptyTextView.setVisibility(View.GONE);
        }

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                final Alarm alarm = alarmsAdapter.getItem(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Delete");
                dialog.setMessage("Delete this alarm?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Database.init(getActivity());
                        Database.deleteEntry(alarm);
                        ((MainActivity) getActivity()).callMathAlarmScheduleService();

                        updateAlarmList();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        updateAlarmList();
        super.onResume();
    }

    public void updateAlarmList(){

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Database.init(getActivity());
                List<Alarm> alarms = Database.getAll();
                int size = alarms.size();
                for (int i = size - 1; i >= 0; i--) {
                    if (alarms.get(i).getEvent()) {
                        alarms.remove(i);
                    }
                }
                alarmsAdapter.setAlarms(alarms);

                // reload content
                alarmsAdapter.notifyDataSetChanged();
                if (alarms.size() > 0) {
                    emptyTextView.setVisibility(View.INVISIBLE);
                } else {
                    emptyTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void callAlarmScheduleService() {
        Intent AlarmServiceIntent = new Intent(getActivity(), AlarmServiceBroadcastReciever.class);
        getActivity().sendBroadcast(AlarmServiceIntent, null);
    }

}
