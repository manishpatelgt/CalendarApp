package me.vucko.calendarapp.alarm.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

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
import java.util.List;
import java.util.Locale;

import me.vucko.calendarapp.MainActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.SyncCalendarsActivity;
import me.vucko.calendarapp.alarm.Alarm;
import me.vucko.calendarapp.alarm.database.Database;
import me.vucko.calendarapp.domain.eventbus_events.AlarmChangeEvent;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SyncCalendarSevice extends Service {

    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int MILLISECONDS_IN_WEEK = 1000 * 60 * 60 * 24 * 6;

    @Override
    public void onCreate() {
        super.onCreate();

        mCredential = SyncCalendarsActivity.mCredential;

        getResultsFromApi();
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
                this, Manifest.permission.GET_ACCOUNTS)) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
                apiAvailability.isGooglePlayServicesAvailable(this);
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
                apiAvailability.isGooglePlayServicesAvailable(this);
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
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean newAlarmAdded = false;
            mProgress.hide();
            if (output != null && output.size() != 0) {
                Database.init(getApplicationContext());
                List<Alarm> alarms = Database.getAll();
                for (Alarm alarm: alarms) {
                    if (alarm.getEvent() || alarm.getAlarmEventTime() != null) {
                        Database.deleteEntry(alarm);
                        alarms.remove(alarm);
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

                    eventCalendar.add(java.util.Calendar.MINUTE, sharedPreferences.getInt("notificationTimePicked", 0) * (-1));
                    int excludeAlarmsBeforeTimePicker = sharedPreferences.getInt("excludeAlarmsBeforeTimePicker", 540);
                    if (!sharedPreferences.getBoolean("eventsBeforeCheckbox", false) ||
                            (eventCalendar.get(Calendar.HOUR_OF_DAY) * 60 + eventCalendar.get(java.util.Calendar.MINUTE)) < excludeAlarmsBeforeTimePicker) {
                        continue;
                    }

                    Alarm alarm1 = new Alarm();
                    alarm1.setAlarmName(name);
                    alarm1.setOneTime(true);
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
            mProgress.hide();
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.newAlarms)))
                    .setAutoCancel(true)
                    .setContentText(getString(R.string.newAlarms))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
