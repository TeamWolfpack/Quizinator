package com.seniordesign.wolfpack.quizinator.Database.HighScore;

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
public class HighScoresDataSource {

    // Database fields
    private SQLiteDatabase database;
    private HighScoresSQLiteHelper dbHelper;
    private String[] allColumns = {
//            ItemSQLiteHelper.COLUMN_ID,
//            ItemSQLiteHelper.COLUMN_FISHTYPE,
//            ItemSQLiteHelper.COLUMN_WEIGHT,
//            ItemSQLiteHelper.COLUMN_LENGTH,
//            ItemSQLiteHelper.COLUMN_DATE,
//            ItemSQLiteHelper.COLUMN_LOCATION
    };

    /*
     * @author  chuna (10/4/2016)
     */
    public HighScoresDataSource(Context context) {
        dbHelper = new HighScoresSQLiteHelper(context);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void close() {
        dbHelper.close();
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public SQLiteDatabase getDatabase(){
        return database;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public HighScoresSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public HighScores createRule(double weight, long date,
                                 String location) {
        ContentValues values = new ContentValues();
//        values.put(SettingsSQLiteHelper.COLUMN_FISHTYPE, "Fish");
//        values.put(SettingsSQLiteHelper.COLUMN_WEIGHT, weight);
//        values.put(SettingsSQLiteHelper.COLUMN_LENGTH, 0.0);
//        values.put(SettingsSQLiteHelper.COLUMN_DATE, date);
//        values.put(SettingsSQLiteHelper.COLUMN_LOCATION, location);
        long insertId = database.insert(HighScoresSQLiteHelper.TABLE_RULES,
                null, values);
        Cursor cursor = database.query(HighScoresSQLiteHelper.TABLE_RULES,
                allColumns, HighScoresSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        HighScores newHighScores = cursorToRule(cursor);
        cursor.close();
        return newHighScores;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void deleteItem(HighScores rule) { //TODO
        long id = rule.getId();
        System.out.println("Deleted item: " + rule.toString());
        database.delete(HighScoresSQLiteHelper.TABLE_RULES,
                HighScoresSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<HighScores> getAllItems() {
        List<HighScores> items = new ArrayList<Item>();
        Cursor cursor = database.query(HighScoresSQLiteHelper.TABLE_RULES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HighScores rule = cursorToRule(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public HighScores cursorToRule(Cursor cursor) {
        HighScores rule = new HighScores();
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
