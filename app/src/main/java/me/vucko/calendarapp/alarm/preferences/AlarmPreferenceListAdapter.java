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
package me.vucko.calendarapp.alarm.preferences;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.preferences.AlarmPreference.Type;
import me.vucko.calendarapp.domain.eventbus_events.AlarmBooleanEditEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmSnoozeTimeEditEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmVolumeEditEvent;

public class AlarmPreferenceListAdapter extends BaseAdapter implements Serializable {

	private Context context;
	private Alarm alarm;
	private List<AlarmPreference> preferences = new ArrayList<AlarmPreference>();
	private final String[] repeatDays = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};	
	private final String[] alarmDifficulties = {"Easy","Medium","Hard"};
	
	private String[] alarmTones;
	private String[] alarmTonePaths;
	
	public AlarmPreferenceListAdapter(Context context, Alarm alarm) {
		setContext(context);
		if (!EventBus.getDefault().isRegistered(context)) {
			EventBus.getDefault().register(context);
		}

//		(new Runnable(){
//
//			@Override
//			public void run() {
				
				RingtoneManager ringtoneMgr = new RingtoneManager(getContext());
				
				ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
				
				Cursor alarmsCursor = ringtoneMgr.getCursor();
				
				alarmTones = new String[alarmsCursor.getCount()+1];
				alarmTones[0] = "Silent"; 
				alarmTonePaths = new String[alarmsCursor.getCount()+1];
				alarmTonePaths[0] = "";
				
				if (alarmsCursor.moveToFirst()) {		    			
					do {
						alarmTones[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtone(alarmsCursor.getPosition()).getTitle(getContext());
						alarmTonePaths[alarmsCursor.getPosition()+1] = ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()).toString();
					}while(alarmsCursor.moveToNext());					
				}
				alarmsCursor.close();
//				
//			}
//			
//		}).run();
//		
	    setMathAlarm(alarm);		
	}

	@Override
	public int getCount() {
		return preferences.size();
	}

	@Override
	public Object getItem(int position) {
		return preferences.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final AlarmPreference alarmPreference = (AlarmPreference) getItem(position);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		switch (alarmPreference.getType()) {
		case BOOLEAN:
			if(null == convertView || convertView.getId() != R.layout.custom_switch_preference)
				convertView = layoutInflater.inflate(R.layout.custom_switch_preference, null);

			TextView textView = (TextView) convertView.findViewById(R.id.textView);
			Switch mySwitch = (Switch) convertView.findViewById(R.id.mySwitch);
			textView.setText(alarmPreference.getTitle());
			mySwitch.setChecked((Boolean) alarmPreference.getValue());
			mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					EventBus.getDefault().post(new AlarmBooleanEditEvent(isChecked, alarmPreference.getKey()));

				}
			});
			break;

		case SEEK_BAR:

			if(null == convertView || convertView.getId() != R.id.volumeSeekBar)
				convertView = layoutInflater.inflate(R.layout.volume_seek_bar, null);
			SeekBar seekBar = (SeekBar) convertView.findViewById(R.id.volumeSeekBar);
			seekBar.setProgress(alarm.getVolume());
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					EventBus.getDefault().post(new AlarmVolumeEditEvent(progress, alarmPreference.getKey()));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}
			});
			break;
			case NUMBER_PICKER:
				if(null == convertView || convertView.getId() != R.id.snoozeNumberPicker)
					convertView = layoutInflater.inflate(R.layout.snooze_number_picker, null);
				final NumberPicker numberPicker = (NumberPicker) convertView.findViewById(R.id.snoozeNumberPicker);
				numberPicker.setMaxValue(30);
				numberPicker.setMinValue(0);
				numberPicker.setEnabled(alarm.getSnooze());
				numberPicker.setValue(alarm.getSnoozeTime());
				numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
					@Override
					public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
						EventBus.getDefault().post(new AlarmSnoozeTimeEditEvent(newVal, alarmPreference.getKey()));
					}
				});
				break;

		case INTEGER:
		case STRING:
		case LIST:
		case MULTIPLE_LIST:
		case TIME:
		default:
			if(null == convertView || convertView.getId() != android.R.layout.simple_list_item_2)
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
			
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			text1.setTextSize(18);
			text1.setText(alarmPreference.getTitle());
			
			TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
			text2.setText(alarmPreference.getSummary());
			break;
		}

		return convertView;
	}

	public Alarm getMathAlarm() {		
		for(AlarmPreference preference : preferences){
			switch(preference.getKey()){
				case ALARM_ACTIVE:
					alarm.setAlarmActive((Boolean) preference.getValue());
					break;
				case ALARM_NAME:
					alarm.setAlarmName((String) preference.getValue());
					break;
				case ALARM_VOLUME:
					alarm.setVolume((Integer) preference.getValue());
					break;
				case ALARM_SNOOZE:
					alarm.setSnooze((Boolean) preference.getValue());
					break;
				case ALARM_TONE:
					alarm.setAlarmTonePath((String) preference.getValue());
					break;
				case ALARM_VIBRATE:
					alarm.setVibrate((Boolean) preference.getValue());
					break;
				case ALARM_REPEAT:
					alarm.setDays((Alarm.Day[]) preference.getValue());
					break;
			}
		}
				
		return alarm;
	}

	public void setMathAlarm(Alarm alarm) {
		this.alarm = alarm;
		preferences.clear();
//		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_ACTIVE,"Active", null, null, alarm.getAlarmActive(),Type.BOOLEAN));
//		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_NAME, "Label",alarm.getAlarmName(), null, alarm.getAlarmName(), Type.STRING));
		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_REPEAT, "Repeat",alarm.getRepeatDaysString(), repeatDays, alarm.getDays(),Type.MULTIPLE_LIST));
			Uri alarmToneUri = Uri.parse(alarm.getAlarmTonePath());
			Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);

		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VOLUME, "Volume",null, null, alarm.getVolume(), Type.SEEK_BAR));

		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_VIBRATE, "Vibrate",null, null, alarm.getVibrate(), Type.BOOLEAN));

		if(alarmTone instanceof Ringtone && !alarm.getAlarmTonePath().equalsIgnoreCase("")){
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Tone", alarmTone.getTitle(getContext()),alarmTones, alarm.getAlarmTonePath(), Type.LIST));
		}else{
			preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_TONE, "Tone", getAlarmTones()[0],alarmTones, null, Type.LIST));
		}

		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_SNOOZE,"Snooze", null, null, alarm.getSnooze(), Type.BOOLEAN));

		preferences.add(new AlarmPreference(AlarmPreference.Key.ALARM_SNOOZE_TIME, "Snooze time", null, null, alarm.getSnoozeTime(), Type.NUMBER_PICKER));
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String[] getRepeatDays() {
		return repeatDays;
	}

	public String[] getAlarmDifficulties() {
		return alarmDifficulties;
	}

	public String[] getAlarmTones() {
		return alarmTones;
	}

	public String[] getAlarmTonePaths() {
		return alarmTonePaths;
	}

}
