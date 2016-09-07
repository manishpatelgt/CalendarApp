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
package me.vucko.calendarapp.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.alert.AlarmAlertBroadcastReciever;
import me.vucko.calendarapp.alarm.database.Database;

public class AlarmServiceBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("AlarmServiceBR", "onReceive()");
//		Intent serviceIntent = new Intent(context, AlarmService.class);
//		context.startService(serviceIntent);

		Alarm alarm = getNext(context);
		if(null != alarm){
			if (!alarm.getEvent()) {
				alarm.schedule(context);
				Log.d(this.getClass().getSimpleName(), alarm.getTimeUntilNextAlarmMessage());
			}
		}else{
			Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
			myIntent.putExtra("alarm", new Alarm());

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

			alarmManager.cancel(pendingIntent);
		}
	}

	private Alarm getNext(Context context){
		Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>() {
			@Override
			public int compare(Alarm lhs, Alarm rhs) {
				int result = 0;
				long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
				if(diff>0){
					return 1;
				}else if (diff < 0){
					return -1;
				}
				return result;
			}
		});

		Database.init(context);
		List<Alarm> alarms = Database.getAll();

		for(Alarm alarm : alarms){
			if(alarm.getAlarmActive())
				alarmQueue.add(alarm);
		}
		if(alarmQueue.iterator().hasNext()){
			return alarmQueue.iterator().next();
		}else{
			return null;
		}
	}
}
