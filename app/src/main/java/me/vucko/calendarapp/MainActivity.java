package me.vucko.calendarapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.database.DBHandler;
import me.vucko.calendarapp.domain.entity.Calendar;
import me.vucko.calendarapp.fragments.FirstFragment;
import me.vucko.calendarapp.fragments.SecondFragment;
import me.vucko.calendarapp.fragments.ThirdFragment;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHandler db = new DBHandler(this);
        db.addCalendar(new Calendar(12345, "blabla"));
        Log.i("added to database", "added");
        Log.i("sveee", db.getAllCalendars().toString());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return FirstFragment.newInstance();
            }
            if(position == 1){
                return SecondFragment.newInstance();
            }
            return ThirdFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Alarm";
                case 1:
                    return "Calendar";
                case 2:
                    return "Settings";
            }
            return null;
        }
    }

    public void callMathAlarmScheduleService() {
        Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
        sendBroadcast(mathAlarmServiceIntent, null);
    }
}
