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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.vucko.calendarapp.AlarmsAdapter;
import me.vucko.calendarapp.MainActivity;
import me.vucko.calendarapp.MoreSettingsActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;

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
        alarmsAdapter = new AlarmsAdapter(context, R.layout.custom_alarm_entry, new ArrayList<Alarm>());
        Database.init(context);
        final List<Alarm> alarms = Database.getAll();
        alarmsAdapter.setAlarms(alarms);
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
        ListView alarmListView = (ListView) view.findViewById(R.id.alarms_listview);
        Button setButton = (Button) view.findViewById(R.id.setButton);
        Button moreSettingsButton = (Button) view.findViewById(R.id.moreSettingsButton);
        moreSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreSettingsActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        alarmListView.setAdapter(alarmsAdapter);
        emptyTextView = (TextView) view.findViewById(android.R.id.empty);
        if(alarmsAdapter.getCount() > 0){
            emptyTextView.setVisibility(View.GONE);
        }
        alarmListView.setClickable(true);
        alarmListView.setLongClickable(true);
        alarmListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
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

                return true;
            }
        });

        return view;
    }

    public void updateAlarmList(){
        Database.init(getActivity());
        final List<Alarm> alarms = Database.getAll();
        alarmsAdapter.setAlarms(alarms);

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
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

}
