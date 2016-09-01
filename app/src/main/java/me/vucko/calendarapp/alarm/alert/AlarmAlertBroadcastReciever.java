/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package me.vucko.calendarapp.alarm.alert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;

import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.eventbus_events.SyncEvents;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

    private static final int MILLISECONDS_IN_DAY = 60*60*24*1000;

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);

        Log.i("onReceive", "BRTTTT USAO sam ovde i sad cemo vidit ocu li zvonit");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean("nijeAlarm", false)) {
            EventBus.getDefault().post(new SyncEvents());

            Date d = new Date();
            Calendar today = Calendar.getInstance();
            today.setTime(d);
            today.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("notificationTimePicker", 0) / 60);
            today.set(Calendar.MINUTE, sharedPreferences.getInt("notificationTimePicker", 0) % 60 - 1);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            today.add(Calendar.MILLISECOND, MILLISECONDS_IN_DAY);

            Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
            myIntent.putExtra("nijeAlarm", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pendingIntent);
        } else {
            int time = sharedPreferences.getInt("eventsBeforeTimePicker", 0);
            if (sharedPreferences.getBoolean("eventsBeforeCheckbox", false) && shouldSoundTheAlarm(time, Calendar.getInstance())) {
                StaticWakeLock.lockOn(context);
                Bundle bundle = intent.getExtras();
                final Alarm alarm = (Alarm) bundle.getSerializable("alarm");

                Intent mathAlarmAlertActivityIntent;

                mathAlarmAlertActivityIntent = new Intent(context, AlarmAlertActivity.class);

                mathAlarmAlertActivityIntent.putExtra("alarm", alarm);

                mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(mathAlarmAlertActivityIntent);
            }
        }
    }

	private boolean shouldSoundTheAlarm(int totalTime, Calendar time){
		int currentHour = time.getTime().getHours();
		int currentMinute = time.getTime().getMinutes();
        return (totalTime) < (currentHour * 60 + currentMinute);
    }

}
