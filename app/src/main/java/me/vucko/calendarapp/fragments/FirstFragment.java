package me.vucko.calendarapp.fragments;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.MainActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.adapters.AlarmsAdapter;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.alert.AlarmAlertBroadcastReciever;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.preferences.AlarmPreferencesActivity;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;

public class FirstFragment extends Fragment {

    private static final int MILLISECONDS_IN_DAY = 60*60*24*1000;
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
        Calendar calendar = Calendar.getInstance();
        for (int i = size - 1; i >= 0; i--) {
            if (alarms.get(i).getEvent() || (!Arrays.asList(alarms.get(i).getDays()).contains(convert(calendar.get(java.util.Calendar.DAY_OF_WEEK))))) {
                alarms.remove(i);
            }
        }
        alarmsAdapter = new AlarmsAdapter(context, alarms);
        alarmsAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!sharedPreferences.getBoolean("cronJob", false)) {
            Date d = new Date();
            Calendar today = Calendar.getInstance();
            today.setTime(d);
            today.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("caficationTimePicker", 0) / 60);
            today.set(Calendar.MINUTE, sharedPreferences.getInt("notificationTimePicker", 0) % 60);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
            myIntent.putExtra("nijeAlarm", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pendingIntent);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("cronJob", true);
            editor.apply();
        }
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
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                Alarm alarm = new Alarm(getNextDay(hour, minute));
                alarm.setAlarmTime(hour + ":" + minute);
                alarm.setOneTime(true);
                Database.init(getActivity());
                Database.create(alarm);
                callAlarmScheduleService();
                updateAlarmList();
                EventBus.getDefault().post(new AlarmChangeEvent());
            }
        });
        Button moreSettingsButton = (Button) view.findViewById(R.id.moreSettingsButton);
        moreSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmPreferencesActivity.class);
                Alarm alarm = new Alarm();
                alarm.setAlarmTime(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                intent.putExtra("alarm", alarm);
//                intent.putExtra("timePickerTimeHour", timePicker.getCurrentHour());
//                intent.putExtra("timePickerTimeMinute", timePicker.getCurrentMinute());
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
        if (alarmsAdapter.getCount() > 0) {
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

    public void updateAlarmList() {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Database.init(getActivity());
                List<Alarm> alarms = Database.getAll();
                int size = alarms.size();
                Calendar calendar = Calendar.getInstance();
                for (int i = size - 1; i >= 0; i--) {
                        if (alarms.get(i).getEvent() || (!Arrays.asList(alarms.get(i).getDays()).contains(convert(calendar.get(java.util.Calendar.DAY_OF_WEEK))))) {
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

    private Alarm.Day[] getNextDay(int hour, int minute) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        boolean nextDay = false;
        int currentHour = calendar.getTime().getHours();
        int currentMinute = calendar.getTime().getMinutes();
        if (hour < currentHour || (hour == currentHour && minute < currentMinute)) {
            nextDay = true;
        }
        int day = calendar.getTime().getDay();
        if (nextDay) day = (day + 1) % 7;

        switch (day) {
            case 0:
                return new Alarm.Day[]{Alarm.Day.SUNDAY};

            case 1:
                return new Alarm.Day[]{Alarm.Day.MONDAY};

            case 2:
                return new Alarm.Day[]{Alarm.Day.TUESDAY};

            case 3:
                return new Alarm.Day[]{Alarm.Day.WEDNESDAY};

            case 4:
                return new Alarm.Day[]{Alarm.Day.THURSDAY};

            case 5:
                return new Alarm.Day[]{Alarm.Day.FRIDAY};

            default:
                return new Alarm.Day[]{Alarm.Day.SATURDAY};

        }
    }

    private Alarm.Day convert(int day) {
        switch (day) {
            case 1 : return Alarm.Day.SUNDAY;
            case 2 : return Alarm.Day.MONDAY;
            case 3 : return Alarm.Day.TUESDAY;
            case 4 : return Alarm.Day.WEDNESDAY;
            case 5 : return Alarm.Day.THURSDAY;
            case 6 : return Alarm.Day.FRIDAY;
            default : return Alarm.Day.SATURDAY;
        }
    }

}
