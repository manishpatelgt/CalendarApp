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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import me.vucko.calendarapp.R;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.BaseActivity;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.preferences.AlarmPreference.Key;
import me.vucko.calendarapp.domain.eventbus_events.AlarmBooleanEditEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmSnoozeTimeEditEvent;
import me.vucko.calendarapp.domain.eventbus_events.AlarmVolumeEditEvent;

public class AlarmPreferencesActivity extends BaseActivity {

	ImageButton deleteButton;
	TextView okButton;
	TextView cancelButton;
	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	private ListAdapter listAdapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.alarm_preferences);
		EventBus.getDefault().register(this);

		TimePicker timePicker = (TimePicker) findViewById(R.id.timePickerMoreSettings);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("alarm")) {
			Alarm alarm = (Alarm) bundle.getSerializable("alarm");
			setMathAlarm(alarm);
			if (timePicker != null) {
				timePicker.setCurrentHour(alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(alarm.getAlarmTime().get(Calendar.MINUTE));
			}
		} else {
			setMathAlarm(new Alarm());
		}
		if (bundle != null && bundle.containsKey("adapter")) {
			setListAdapter((AlarmPreferenceListAdapter) bundle.getSerializable("adapter"));
		} else {
			setListAdapter(new AlarmPreferenceListAdapter(this, getMathAlarm()));
		}

		if (timePicker != null) {
//			Bundle extras = getIntent().getExtras();
//			if (extras != null){
//				timePicker.setCurrentHour(extras.getInt("timePickerTimeHour", 1));
//				timePicker.setCurrentMinute(extras.getInt("timePickerTimeMinute", 0));
//			}

			timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					Calendar newAlarmTime = Calendar.getInstance();
					newAlarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
					newAlarmTime.set(Calendar.MINUTE, minute);
					newAlarmTime.set(Calendar.SECOND, 0);
					alarm.setAlarmTime(newAlarmTime);
				}
			});
		}

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				final AlarmPreferenceListAdapter alarmPreferenceListAdapter = (AlarmPreferenceListAdapter) getListAdapter();
				final AlarmPreference alarmPreference = (AlarmPreference) alarmPreferenceListAdapter.getItem(position);

				AlertDialog.Builder alert;
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				switch (alarmPreference.getType()) {
				case BOOLEAN:
					CheckedTextView checkedTextView = (CheckedTextView) v;
					boolean checked = !checkedTextView.isChecked();
					((CheckedTextView) v).setChecked(checked);
					switch (alarmPreference.getKey()) {
					case ALARM_SNOOZE:
						alarm.setSnooze(checked);
						break;
					case ALARM_ACTIVE:
						alarm.setAlarmActive(checked);
						break;
					case ALARM_VIBRATE:
						alarm.setVibrate(checked);
						if (checked) {
							Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
							vibrator.vibrate(1000);
						}
						break;
					}
					alarmPreference.setValue(checked);
					break;
				case STRING:

					alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);

					alert.setTitle(alarmPreference.getTitle());
					// alert.setMessage(message);

					// Set an EditText view to get user input
					final EditText input = new EditText(AlarmPreferencesActivity.this);

					input.setText(alarmPreference.getValue().toString());

					alert.setView(input);
					alert.setPositiveButton("Ok", new OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							alarmPreference.setValue(input.getText().toString());

							if (alarmPreference.getKey() == Key.ALARM_NAME) {
								alarm.setAlarmName(alarmPreference.getValue().toString());
							}

							alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
							alarmPreferenceListAdapter.notifyDataSetChanged();
						}
					});
					alert.show();
					break;
				case LIST:
					alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);

					alert.setTitle(alarmPreference.getTitle());
					// alert.setMessage(message);

					CharSequence[] items = new CharSequence[alarmPreference.getOptions().length];
					for (int i = 0; i < items.length; i++)
						items[i] = alarmPreference.getOptions()[i];

					alert.setItems(items, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (alarmPreference.getKey()) {
							case ALARM_TONE:
								alarm.setAlarmTonePath(alarmPreferenceListAdapter.getAlarmTonePaths()[which]);
								if (alarm.getAlarmTonePath() != null) {
									if (mediaPlayer == null) {
										mediaPlayer = new MediaPlayer();
									} else {
										if (mediaPlayer.isPlaying())
											mediaPlayer.stop();
										mediaPlayer.reset();
									}
									try {
										// mediaPlayer.setVolume(1.0f, 1.0f);
										mediaPlayer.setVolume(0.2f, 0.2f);
										mediaPlayer.setDataSource(AlarmPreferencesActivity.this, Uri.parse(alarm.getAlarmTonePath()));
										mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
										mediaPlayer.setLooping(false);
										mediaPlayer.prepare();
										mediaPlayer.start();

										// Force the mediaPlayer to stop after 3
										// seconds...
										if (alarmToneTimer != null)
											alarmToneTimer.cancel();
										alarmToneTimer = new CountDownTimer(3000, 3000) {
											@Override
											public void onTick(long millisUntilFinished) {

											}

											@Override
											public void onFinish() {
												try {
													if (mediaPlayer.isPlaying())
														mediaPlayer.stop();
												} catch (Exception e) {

												}
											}
										};
										alarmToneTimer.start();
									} catch (Exception e) {
										try {
											if (mediaPlayer.isPlaying())
												mediaPlayer.stop();
										} catch (Exception e2) {

										}
									}
								}
								break;
							default:
								break;
							}
							alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
							alarmPreferenceListAdapter.notifyDataSetChanged();
						}

					});

					alert.show();
					break;
				case MULTIPLE_LIST:
					alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);

					alert.setTitle(alarmPreference.getTitle());
					// alert.setMessage(message);

					CharSequence[] multiListItems = new CharSequence[alarmPreference.getOptions().length];
					for (int i = 0; i < multiListItems.length; i++)
						multiListItems[i] = alarmPreference.getOptions()[i];

					boolean[] checkedItems = new boolean[multiListItems.length];
					for (Alarm.Day day : getMathAlarm().getDays()) {
						checkedItems[day.ordinal()] = true;
					}
					alert.setMultiChoiceItems(multiListItems, checkedItems, new OnMultiChoiceClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, int which, boolean isChecked) {

							Alarm.Day thisDay = Alarm.Day.values()[which];

							if (isChecked) {
								alarm.addDay(thisDay);
							} else {
								// Only remove the day if there are more than 1
								// selected
								if (alarm.getDays().length > 1) {
									alarm.removeDay(thisDay);
								} else {
									// If the last day was unchecked, re-check
									// it
									((AlertDialog) dialog).getListView().setItemChecked(which, true);
								}
							}

						}
					});
					alert.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							alarmPreferenceListAdapter.setMathAlarm(getMathAlarm());
							alarmPreferenceListAdapter.notifyDataSetChanged();

						}
					});
					alert.show();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_save:
			Database.init(getApplicationContext());
			if (getMathAlarm().getId() < 1) {
				Database.create(getMathAlarm());
			} else {
				Database.update(getMathAlarm());
			}
			EventBus.getDefault().post(new AlarmChangeEvent());
			callMathAlarmScheduleService();
			Toast.makeText(AlarmPreferencesActivity.this, getMathAlarm().getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private CountDownTimer alarmToneTimer;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("alarm", getMathAlarm());
		outState.putSerializable("adapter", (AlarmPreferenceListAdapter) getListAdapter());
	};

	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (mediaPlayer != null)
				mediaPlayer.release();
		} catch (Exception e) {
		}
		// setListAdapter(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public Alarm getMathAlarm() {
		return alarm;
	}

	public void setMathAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

	public ListAdapter getListAdapter() {
		return listAdapter;
	}

	public void setListAdapter(ListAdapter listAdapter) {
		this.listAdapter = listAdapter;
		getListView().setAdapter(listAdapter);

	}

	public ListView getListView() {
		if (listView == null)
			listView = (ListView) findViewById(android.R.id.list);
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	@Override
	public void onClick(View v) {
		// super.onClick(v);

	}

	@Subscribe
	public void onAlarmBooleanEditEvent(AlarmBooleanEditEvent alarmBooleanEditEvent) {
		switch (alarmBooleanEditEvent.getKey()) {
			case ALARM_VIBRATE:
				alarm.setVibrate(alarmBooleanEditEvent.getIsChecked());
				break;
			case ALARM_SNOOZE:
				alarm.setSnooze(alarmBooleanEditEvent.getIsChecked());
				break;
		}
	}

	@Subscribe
	public void onAlarmVolumeEditEvent(AlarmVolumeEditEvent alarmVolumeEditEvent) {
		alarm.setVolume(alarmVolumeEditEvent.getVolume());
	}

	@Subscribe
	public void onAlarmSnoozeTimeEditEvent(AlarmSnoozeTimeEditEvent alarmSnoozeTimeEditEvent) {
		alarm.setSnoozeTime(alarmSnoozeTimeEditEvent.getTime());
	}
}
