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
//            ItemSQLiteHelper.COLUMN_ID,
//            ItemSQLiteHelper.COLUMN_FISHTYPE,
//            ItemSQLiteHelper.COLUMN_WEIGHT,
//            ItemSQLiteHelper.COLUMN_LENGTH,
//            ItemSQLiteHelper.COLUMN_DATE,
//            ItemSQLiteHelper.COLUMN_LOCATION
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
    public Settings createRule(double weight, long date,
                               String location) {
        ContentValues values = new ContentValues();
//        values.put(SettingsSQLiteHelper.COLUMN_FISHTYPE, "Fish");
//        values.put(SettingsSQLiteHelper.COLUMN_WEIGHT, weight);
//        values.put(SettingsSQLiteHelper.COLUMN_LENGTH, 0.0);
//        values.put(SettingsSQLiteHelper.COLUMN_DATE, date);
//        values.put(SettingsSQLiteHelper.COLUMN_LOCATION, location);
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
    public void deleteItem(Settings rule) { //TODO
        //long id = rule.getId();
        System.out.println("Deleted item: " + rule.toString());
        database.delete(SettingsSQLiteHelper.TABLE_RULES,
                SettingsSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public List<Settings> getAllItems() {
        List<Settings> items = new ArrayList<Settings>();
        Cursor cursor = database.query(SettingsSQLiteHelper.TABLE_RULES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Settings rule = cursorToRule(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public Settings cursorToRule(Cursor cursor) {
        Settings rule = new Settings();
//        rule.setId(cursor.getLong(0));//id
//        rule.setFishType(cursor.getString(1));//fishType
//        rule.setWeight(cursor.getDouble(2));//weight
//        rule.setLength(cursor.getDouble(3));//length
//        rule.setDate(cursor.getLong(4));//date
//        rule.setLocation(cursor.getString(5));//location
        return rule;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
