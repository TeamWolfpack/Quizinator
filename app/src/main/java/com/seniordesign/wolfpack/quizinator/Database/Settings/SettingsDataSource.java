package com.seniordesign.wolfpack.quizinator.Database.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for settings
 * @creation 10/4/2016.
 */
public class SettingsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SettingsSQLiteHelper dbHelper;
    private String[] allColumns = {
        SettingsSQLiteHelper.COLUMN_ID,
        SettingsSQLiteHelper.COLUMN_USERNAME,
        SettingsSQLiteHelper.COLUMN_NUMBEROFCONNECTIONS
    };

    /*
     * @author kuczynskij (10/4/2016)
     */
    public SettingsDataSource(Context context) {
        dbHelper = new SettingsSQLiteHelper(context);
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return database.isOpen();
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public boolean close() {
        dbHelper.close();
        return true;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public SQLiteDatabase getDatabase(){
        return database;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public SettingsSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public Settings createSettings(int numberOfConnections, String userName) {
        ContentValues values = new ContentValues();
            values.put(SettingsSQLiteHelper.COLUMN_USERNAME, userName);
            values.put(SettingsSQLiteHelper.COLUMN_NUMBEROFCONNECTIONS, numberOfConnections);
        long insertId = database.insert(SettingsSQLiteHelper.TABLE_SETTINGS,
                null, values);
        Cursor cursor = database.query(SettingsSQLiteHelper.TABLE_SETTINGS,
                allColumns, SettingsSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Settings newSettings = cursorToRule(cursor);
        cursor.close();
        return newSettings;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public boolean deleteSetting(Settings settings) {
        long id = settings.getId();
        database.delete(SettingsSQLiteHelper.TABLE_SETTINGS,
                SettingsSQLiteHelper.COLUMN_ID + " = " + id, null);
        return true;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public List<Settings> getAllSettings() {
        List<Settings> settings = new ArrayList<>();
        Cursor cursor = database.query(SettingsSQLiteHelper.TABLE_SETTINGS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Settings s = cursorToRule(cursor);
            settings.add(s);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return settings;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public Settings cursorToRule(Cursor cursor) {
        Settings settings = new Settings();
            settings.setId(cursor.getLong(0));//id
            settings.setUserName(cursor.getString(1));//userName
            settings.setNumberOfConntections(cursor.getInt(2));//numberOfConnections
        return settings;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
