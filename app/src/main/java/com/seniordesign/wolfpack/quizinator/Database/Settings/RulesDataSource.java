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
public class RulesDataSource {

    // Database fields
    private SQLiteDatabase database;
    private RulesSQLiteHelper dbHelper;
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
    public RulesDataSource(Context context) {
        dbHelper = new RulesSQLiteHelper(context);
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
    public RulesSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Rules createRule(double weight, long date,
                            String location) {
        ContentValues values = new ContentValues();
//        values.put(RulesSQLiteHelper.COLUMN_FISHTYPE, "Fish");
//        values.put(RulesSQLiteHelper.COLUMN_WEIGHT, weight);
//        values.put(RulesSQLiteHelper.COLUMN_LENGTH, 0.0);
//        values.put(RulesSQLiteHelper.COLUMN_DATE, date);
//        values.put(RulesSQLiteHelper.COLUMN_LOCATION, location);
        long insertId = database.insert(RulesSQLiteHelper.TABLE_RULES,
                null, values);
        Cursor cursor = database.query(RulesSQLiteHelper.TABLE_RULES,
                allColumns, RulesSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Rules newRules = cursorToRule(cursor);
        cursor.close();
        return newRules;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void deleteItem(Rules rule) { //TODO
        long id = rule.getId();
        System.out.println("Deleted item: " + rule.toString());
        database.delete(RulesSQLiteHelper.TABLE_RULES,
                RulesSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Rules> getAllItems() {
        List<Rules> items = new ArrayList<Item>();
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
     * @author  chuna (10/4/2016)
     */
    public Rules cursorToRule(Cursor cursor) {
        Rules rule = new Rules();
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
