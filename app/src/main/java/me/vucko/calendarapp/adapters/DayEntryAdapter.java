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
        CalendarDay calendarDay = calendarDays.get(position);
        viewHolder.dayLetterTextView.setText(calendarDay.getDayLetter());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        viewHolder.dayDateTextView.setText(simpleDateFormat.format(calendarDay.getDayDate()));

        return convertView;
    }

    private static class ViewHolder {
        TextView dayLetterTextView;
        TextView dayDateTextView;
    }
}
