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

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.MainActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.SyncCalendarsActivity;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.alarm.service.AlarmServiceBroadcastReciever;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    private static final int MILLISECONDS_IN_DAY = 60*60*24*1000;
    private Context context;
    GoogleAccountCredential mCredential;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int MILLISECONDS_IN_WEEK = 1000 * 60 * 60 * 24 * 6;

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent mathAlarmServiceIntent = new Intent(
				context,
				AlarmServiceBroadcastReciever.class);
		context.sendBroadcast(mathAlarmServiceIntent, null);
        this.context = context;

        Log.i("onReceive", "BRTTTT USAO sam ovde i sad cemo vidit ocu li zvonit");

        Bundle bundle = intent.getExtras();
        final Alarm alarm = (Alarm) bundle.getSerializable("alarm");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (alarm == null) {
            Date d = new Date();
            Calendar today = Calendar.getInstance();
            today.setTime(d);
            today.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("notificationTimePicker", 0) / 60);
            today.set(Calendar.MINUTE, sharedPreferences.getInt("notificationTimePicker", 0) % 60);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            today.add(Calendar.MILLISECOND, MILLISECONDS_IN_DAY);

            Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
            myIntent.putExtra("nijeAlarm", true);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), pendingIntent);

            mCredential = GoogleAccountCredential.usingOAuth2(
                    context, Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

            getResultsFromApi();
        } else {
            int time = sharedPreferences.getInt("eventsBeforeTimePicker", 0);
            if (!sharedPreferences.getBoolean("eventsBeforeCheckbox", false) || ((sharedPreferences.getBoolean("eventsBeforeCheckbox", false)) && shouldSoundTheAlarm(time, Calendar.getInstance()))) {
                StaticWakeLock.lockOn(context);

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

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
//            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                context, Manifest.permission.GET_ACCOUNTS)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            String accountName = preferences
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
    }



    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
//            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean newAlarmAdded = false;
//            mProgress.hide();
            if (output != null && output.size() != 0) {
                Database.init(context);
                List<Alarm> alarms = Database.getAll();
                int size = alarms.size();
                for (int i = size - 1; i >= 0; i--) {
                    if (alarms.get(i).getEvent() || alarms.get(i).getAlarmEventTime() != null || alarms.get(i).getOneTime()) {
                        Database.deleteEntry(alarms.get(i));
                        alarms.remove(i);
                    }
                }
                for (int i = 0; i < output.size(); i++) {
                    DateFormat df = new SimpleDateFormat("(yyyy-MM-dd'T'HH:mm:ss.SSSZ)", Locale.getDefault());

                    java.util.Calendar eventCalendar = java.util.Calendar.getInstance();
                    try {
                        eventCalendar.setTime(df.parse(output.get(i).substring(output.get(i).lastIndexOf(" ") + 1)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String name = output.get(i).substring(0, output.get(i).lastIndexOf(" "));

                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    if ((eventCalendar.getTimeInMillis() - calendar.getTimeInMillis()) > MILLISECONDS_IN_WEEK) {
                        continue;
                    }

                    Alarm alarm = new Alarm();
                    alarm.setEvent(true);
                    alarm.setOneTime(true);
                    alarm.setAlarmName(name);
                    alarm.setAlarmEventTime(eventCalendar);
                    Database.create(alarm);

                    int excludeAlarmsBeforeTimePicker = sharedPreferences.getInt("excludeAlarmsBeforeTimePicker", 540);
                    if (!sharedPreferences.getBoolean("eventsBeforeCheckbox", false) ||
                            (eventCalendar.get(Calendar.HOUR_OF_DAY) * 60 + eventCalendar.get(java.util.Calendar.MINUTE)) < excludeAlarmsBeforeTimePicker) {
                        continue;
                    }

                    Alarm alarm1 = new Alarm();
                    alarm1.setAlarmName(name);
                    alarm1.setOneTime(true);
                    eventCalendar.add(Calendar.MINUTE, -sharedPreferences.getInt("eventsBeforeFirstAlarmByTimePicker", 0));
                    alarm1.setAlarmTime(eventCalendar);
                    alarm1.setDays(convert(eventCalendar.get(java.util.Calendar.DAY_OF_WEEK)));
                    Database.create(alarm1);

                    newAlarmAdded = true;
                }
                EventBus.getDefault().post(new AlarmChangeEvent());
                if (newAlarmAdded) {
                    sendNotification();
                }
            }
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
//                mOutputText.setText("Request cancelled.");
            }
        }

        private void sendNotification() {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.newAlarms)))
                    .setAutoCancel(true)
                    .setContentText(context.getString(R.string.newAlarms))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private Alarm.Day[] convert(int day) {
        switch (day) {
            case 1 : return new Alarm.Day[]{Alarm.Day.SUNDAY};
            case 2 : return new Alarm.Day[]{Alarm.Day.MONDAY};
            case 3 : return new Alarm.Day[]{Alarm.Day.TUESDAY};
            case 4 : return new Alarm.Day[]{Alarm.Day.WEDNESDAY};
            case 5 : return new Alarm.Day[]{Alarm.Day.THURSDAY};
            case 6 : return new Alarm.Day[]{Alarm.Day.FRIDAY};
            default : return new Alarm.Day[]{Alarm.Day.SATURDAY};
        }
    }

}
