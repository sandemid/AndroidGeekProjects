package com.example.androidgeekproject.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBTables {
    private final static String TABLE_NAME_CITIES = "Cities";
    private final static String COLUMN_CITIES_ID = "_id";
    private final static String COLUMN_CITIES_NAME = "name";
    private final static String COLUMN_CITIES_TITLE = "title";
    private final static String TABLE_NAME_WEATHER = "Weather";
    private final static String COLUMN_WEATHER_ID = "_id";
    private final static String COLUMN_WEATHER_CITYID = "cityID";
    private final static String COLUMN_WEATHER_TIME = "time";
    private final static String COLUMN_WEATHER_TEMP = "temperature";
    private final static String COLUMN_WEATHER_DETAILS = "details";

    public static void createTables(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_NAME_CITIES + " (" + COLUMN_CITIES_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CITIES_NAME + " INTEGER);");
        database.execSQL("CREATE TABLE " + TABLE_NAME_WEATHER + " (" + COLUMN_WEATHER_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_WEATHER_CITYID + " INTEGER,"
                + COLUMN_WEATHER_TIME + " NUMERIC," + COLUMN_WEATHER_TEMP + " TEXT," + COLUMN_WEATHER_DETAILS + " TEXT);");
    }

    public static void onUpgrade(SQLiteDatabase database) {
        database.execSQL("ALTER TABLE " + TABLE_NAME_CITIES + " ADD COLUMN " + COLUMN_CITIES_TITLE
                + " TEXT DEFAULT 'Default title'");
    }

    public static void addWeather(String city, String temp, String date, String details, SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE_NAME_CITIES, null, COLUMN_CITIES_NAME + " = ?", new String[]{city}, null, null, null);

        if(cursor == null || !cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CITIES_NAME, city);
            database.insert(TABLE_NAME_CITIES, null, values);
            cursor = database.query(TABLE_NAME_CITIES, null, COLUMN_CITIES_NAME + " = ?", new String[]{city}, null, null, null);
            cursor.moveToFirst();
        }

        int cityID = cursor.getInt(cursor.getColumnIndex(COLUMN_CITIES_ID));
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEATHER_CITYID, cityID);
        values.put(COLUMN_WEATHER_TIME, date);
        values.put(COLUMN_WEATHER_TEMP, temp);
        values.put(COLUMN_WEATHER_DETAILS, details);
        database.insert(TABLE_NAME_WEATHER, null, values);
    }


    public static List<String[]> getWeather(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT c.name as name, w.time as time, w.temperature as temperature, w.details as details"
                         + " FROM Cities c inner join Weather w on w.cityID = c._id", null);
        return getWeatherFromCursor(cursor);
    }

    public static List<String[]> getWeather(String city, SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT c.name as name, w.time as time, w.temperature as temperature, w.details as details"
                + " FROM Cities c inner join Weather w on w.cityID = c._id where c.name = '" + city
                + "' order by w.time desc", null);
        return getWeatherFromCursor(cursor);
    }

    private static List<String[]> getWeatherFromCursor(Cursor cursor) {
        List<String[]> result = null;

        if(cursor != null && cursor.moveToFirst()) {
            result = new ArrayList<>(cursor.getCount());

            int cityIdx = cursor.getColumnIndex(COLUMN_CITIES_NAME);
            int dateIdx = cursor.getColumnIndex(COLUMN_WEATHER_TIME);
            int tempIdx = cursor.getColumnIndex(COLUMN_WEATHER_TEMP);
            int detailsIdx = cursor.getColumnIndex(COLUMN_WEATHER_DETAILS);
            int i = 0;
            do {
                result.add(new String[4]);
                result.get(i)[0] = cursor.getString(cityIdx);
                result.get(i)[1] = cursor.getString(dateIdx);
                result.get(i)[2] = cursor.getString(tempIdx);
                result.get(i)[3] = cursor.getString(detailsIdx);
                i++;
            } while (cursor.moveToNext());
        }

        try { cursor.close(); } catch (Exception ignored) {}
        return result == null ? new ArrayList<>(0) : result;
    }

    public static String[] getCity (SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE_NAME_CITIES, new String[]{COLUMN_CITIES_NAME}, null, null, null, null, null);
        return getCityFromCursor(cursor);
    }

    private static String[] getCityFromCursor(Cursor cursor) {
        String[] result = null;

        if(cursor != null && cursor.moveToFirst()) {
            result = new String[cursor.getCount()];

            int cityIdx = cursor.getColumnIndex(COLUMN_CITIES_NAME);

            int i = 0;
            do {
                result[i] = cursor.getString(cityIdx);
                i++;
            } while (cursor.moveToNext());
        }

        try { cursor.close(); } catch (Exception ignored) {}
        return result == null ? new String[]{} : result;
    }

}
