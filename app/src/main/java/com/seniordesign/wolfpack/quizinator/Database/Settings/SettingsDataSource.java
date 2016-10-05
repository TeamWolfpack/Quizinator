package com.seniordesign.wolfpack.quizinator.Database.Settings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
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
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public void close() {
        dbHelper.close();
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
        long insertId = database.insert(SettingsSQLiteHelper.TABLE_RULES,
                null, values);
        Cursor cursor = database.query(SettingsSQLiteHelper.TABLE_RULES,
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
    public void deleteItem(Settings settings) {
        long id = settings.getId();
        database.delete(SettingsSQLiteHelper.TABLE_RULES,
                SettingsSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public List<Settings> getAllItems() {
        List<Settings> settings = new ArrayList<>();
        Cursor cursor = database.query(SettingsSQLiteHelper.TABLE_RULES,
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
