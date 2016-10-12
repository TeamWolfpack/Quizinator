package com.seniordesign.wolfpack.quizinator.Database.Rules;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for rules
 * @creation 10/4/2016.
 */
public class RulesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private RulesSQLiteHelper dbHelper;
    private String[] allColumns = {
        RulesSQLiteHelper.COLUMN_ID,
        RulesSQLiteHelper.COLUMN_TIMELIMIT,
        RulesSQLiteHelper.COLUMN_CARDDISPLAYTIME,
        RulesSQLiteHelper.COLUMN_MAXCARDCOUNT,
        RulesSQLiteHelper.COLUMN_CARDTYPES
    };

    /*
     * @author chuna (10/4/2016)
     */
    public RulesDataSource(Context context) {
        dbHelper = new RulesSQLiteHelper(context);
    }

    /*
     * @author chuna (10/4/2016)
     */
    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return database.isOpen();
    }

    /*
     * @author chuna (10/4/2016)
     */
    public boolean close() {
        dbHelper.close();
        return true;
    }

    /*
     * @author chuna (10/4/2016)
     */
    public SQLiteDatabase getDatabase(){
        return database;
    }

    /*
     * @author chuna (10/4/2016)
     */
    public RulesSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author chuna (10/4/2016)
     * @author kuczynskij (10/10/2016)
     */
    public Rules createRule(int maxCardCount, long timeLimit,
                           long cardDisplayTime, String cardTypes) {
        ContentValues values = new ContentValues();
            values.put(RulesSQLiteHelper.COLUMN_TIMELIMIT, timeLimit);
            values.put(RulesSQLiteHelper.COLUMN_CARDDISPLAYTIME, cardDisplayTime);
            values.put(RulesSQLiteHelper.COLUMN_TIMELIMIT, maxCardCount);
            values.put(RulesSQLiteHelper.COLUMN_CARDTYPES, cardTypes);
        long insertId = database.insert(RulesSQLiteHelper.TABLE_RULES,
                null, values);
        Cursor cursor = database.query(RulesSQLiteHelper.TABLE_RULES,
                allColumns, RulesSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Rules newRules = cursorToRule(cursor);
        cursor.close();
        return newRules;
    }

    /*
     * @author chuna (10/4/2016)
     */
    public void deleteRule(Rules rule) {
        long id = rule.getId();
        database.delete(RulesSQLiteHelper.TABLE_RULES,
            RulesSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author chuna (10/4/2016)
     */
    public List<Rules> getAllItems() {
        List<Rules> items = new ArrayList<Rules>();
        Cursor cursor = database.query(RulesSQLiteHelper.TABLE_RULES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rules rule = cursorToRule(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    /*
     * @author chuna (10/4/2016)
     * @author kuczynskij (10/10/2016)
     */
    public Rules cursorToRule(Cursor cursor) {
        Rules rule = new Rules();
            rule.setId(cursor.getLong(0));//id
            rule.setTimeLimit(cursor.getLong(1));//time limit
            rule.setCardDisplayTime(cursor.getLong(2));//card display time
            rule.setMaxCardCount(cursor.getInt(3));//max card count
            rule.setCardTypes(cursor.getString(4));//card types
        return rule;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
