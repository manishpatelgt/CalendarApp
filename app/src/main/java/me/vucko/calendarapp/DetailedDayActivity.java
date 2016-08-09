package me.vucko.calendarapp;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import me.vucko.calendarapp.adapters.AlarmsAdapter;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;

public class DetailedDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ListView alarmsListView = (ListView) findViewById(R.id.alarmsListView);
        AlarmsAdapter alarmsAdapter = new AlarmsAdapter(this);
        assert alarmsListView != null;
        // popuni random alarmima svim iz baze za sad
        Database.init(this);
        final List<Alarm> alarms = Database.getAll();
        alarmsAdapter.setAlarms(alarms);
        alarmsListView.setAdapter(alarmsAdapter);
        alarmsListView.setEmptyView(findViewById(R.id.emptyListView));
    }

}
