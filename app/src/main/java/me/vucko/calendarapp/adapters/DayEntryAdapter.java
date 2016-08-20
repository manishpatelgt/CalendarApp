package me.vucko.calendarapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.domain.CalendarDay;

public class DayEntryAdapter extends ArrayAdapter<CalendarDay> {

    List<CalendarDay> calendarDays = new ArrayList<>();
    Context context;
    LayoutInflater inflater;

    public DayEntryAdapter(Context context, int resource, CalendarDay[] objects) {
        super(context, resource, objects);
        this.context = context;
        calendarDays.addAll(Arrays.asList(objects));
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.custom_day_entry, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayLetterTextView = (TextView) convertView.findViewById(R.id.dayLetterTextView);
            viewHolder.dayDateTextView = (TextView) convertView.findViewById(R.id.dayDateTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TextView alarm1 = (TextView) convertView.findViewById(R.id.alarm1);
        TextView alarm2 = (TextView) convertView.findViewById(R.id.alarm2);
        TextView alarm3 = (TextView) convertView.findViewById(R.id.alarm3);

        TextView event1 = (TextView) convertView.findViewById(R.id.event1);
        TextView event2 = (TextView) convertView.findViewById(R.id.event2);
        TextView event3 = (TextView) convertView.findViewById(R.id.event3);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        if (calendarDays.get(position).getCalendarAlarms().size() > 0) {
            alarm1.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarAlarms().get(0).getAlarmTime().getTime()));
            if (calendarDays.get(position).getCalendarAlarms().get(0).getAlarmTime().get(Calendar.AM_PM) == Calendar.PM)
                alarm1.setText(alarm1.getText() + " PM");
            else
                alarm1.setText(alarm1.getText() + " AM");
        }

        if (calendarDays.get(position).getCalendarAlarms().size() > 1) {
            alarm2.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarAlarms().get(1).getAlarmTime().getTime()));
            if (calendarDays.get(position).getCalendarAlarms().get(1).getAlarmTime().get(Calendar.AM_PM) == Calendar.PM)
                alarm2.setText(alarm2.getText() + " PM");
            else
                alarm2.setText(alarm2.getText() + " AM");
        }

        if (calendarDays.get(position).getCalendarAlarms().size() > 2) {
            alarm3.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarAlarms().get(2).getAlarmTime().getTime()));
            if (calendarDays.get(position).getCalendarAlarms().get(2).getAlarmTime().get(Calendar.AM_PM) == Calendar.PM)
                alarm3.setText(alarm3.getText() + " PM");
            else
                alarm3.setText(alarm3.getText() + " AM");
        }




        if (calendarDays.get(position).getCalendarEvents().size() > 0) {
            event1.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarEvents().get(0).getCalendar().getTime()));
            if (calendarDays.get(position).getCalendarEvents().get(0).getCalendar().get(Calendar.AM_PM) == Calendar.PM)
                event1.setText(event1.getText() + " PM");
            else
                event1.setText(event1.getText() + " AM");
        }

        if (calendarDays.get(position).getCalendarEvents().size() > 1) {
            event2.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarEvents().get(1).getCalendar().getTime()));
            if (calendarDays.get(position).getCalendarEvents().get(1).getCalendar().get(Calendar.AM_PM) == Calendar.PM)
                event1.setText(event2.getText() + " PM");
            else
                event1.setText(event2.getText() + " AM");
        }

        if (calendarDays.get(position).getCalendarEvents().size() > 2) {
            event3.setText(simpleDateFormat.format(calendarDays.get(position).getCalendarEvents().get(2).getCalendar().getTime()));
            if (calendarDays.get(position).getCalendarEvents().get(2).getCalendar().get(Calendar.AM_PM) == Calendar.PM)
                event1.setText(event3.getText() + " PM");
            else
                event1.setText(event3.getText() + " AM");
        }

        CalendarDay calendarDay = calendarDays.get(position);
        viewHolder.dayLetterTextView.setText(calendarDay.getDayLetter());
        viewHolder.dayDateTextView.setText(simpleDateFormat.format(calendarDay.getDayDate()));

        return convertView;
    }

    private static class ViewHolder {
        TextView dayLetterTextView;
        TextView dayDateTextView;
    }
}
