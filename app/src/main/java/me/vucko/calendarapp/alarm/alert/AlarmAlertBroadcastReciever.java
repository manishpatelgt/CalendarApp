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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);

        Log.i("onReceive", "BRTTTT USAO sam ovde i sad cemo vidit ocu li zvonit");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
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

	private boolean shouldSoundTheAlarm(int totalTime, Calendar time){
		int currentHour = time.getTime().getHours();
		int currentMinute = time.getTime().getMinutes();
        return (totalTime) < (currentHour * 60 + currentMinute);
    }

}
