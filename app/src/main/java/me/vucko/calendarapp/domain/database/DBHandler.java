package me.vucko.calendarapp.domain.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import me.vucko.calendarapp.domain.entity.Calendar;

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "sqlite";
    // Contacts table name
    private static final String TABLE_CALENDAR = "calendar";
    // Calendars Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SH_ADDR = "time";
    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SH_ADDR + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);
// Creating tables again
        onCreate(db);
    }

    // Adding new calendar
    public void addCalendar(Calendar calendar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, calendar.getName()); // Calendar Name
        values.put(KEY_SH_ADDR, calendar.getTime()); // Calendar Phone Number

// Inserting Row
        db.insert(TABLE_CALENDAR, null, values);
        db.close(); // Closing database connection
    }
    // Getting one calendar
    public Calendar getCalendar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CALENDAR, new String[]{KEY_ID,
                        KEY_NAME, KEY_SH_ADDR}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor != null) {
            return new Calendar(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), Integer.parseInt(cursor.getString(2)));
        }

        return null;
    }
    // Getting All Calendar
    public List<Calendar> getAllCalendars() {
        List<Calendar> calendarList = new ArrayList<>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CALENDAR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
// Adding contact to list
                calendarList.add(new Calendar(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), Integer.parseInt(cursor.getString(2))));
            } while (cursor.moveToNext());
        }

// return calendar list
        return calendarList;
    }
    // Getting calendar Count
    public int getCalendarCount() {
        String countQuery = "SELECT * FROM " + TABLE_CALENDAR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }
    // Updating a calendar
    public int updateCalendar(Calendar calendar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, calendar.getName());
        values.put(KEY_SH_ADDR, calendar.getTime());

// updating row
        return db.update(TABLE_CALENDAR, values, KEY_ID + " = ?",
                new String[]{String.valueOf(calendar.getId())});
    }

    // Deleting a calendar
    public void deleteCalendar(Calendar calendar) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CALENDAR, KEY_ID + " = ?",
                new String[] { String.valueOf(calendar.getId()) });
        db.close();
    }
}