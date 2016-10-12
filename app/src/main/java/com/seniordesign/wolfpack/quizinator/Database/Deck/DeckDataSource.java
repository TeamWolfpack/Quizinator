package com.seniordesign.wolfpack.quizinator.Database.Deck;

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
public class DeckDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DeckSQLiteHelper dbHelper;
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
    public DeckDataSource(Context context) {
        dbHelper = new DeckSQLiteHelper(context);
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
    public DeckSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Deck createRule(double weight, long date,
                           String location) {
        ContentValues values = new ContentValues();
//        values.put(DeckSQLiteHelper.COLUMN_FISHTYPE, "Fish");
//        values.put(DeckSQLiteHelper.COLUMN_WEIGHT, weight);
//        values.put(DeckSQLiteHelper.COLUMN_LENGTH, 0.0);
//        values.put(DeckSQLiteHelper.COLUMN_DATE, date);
//        values.put(DeckSQLiteHelper.COLUMN_LOCATION, location);
        long insertId = database.insert(DeckSQLiteHelper.TABLE_DECK,
                null, values);
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECK,
                allColumns, DeckSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Deck newRules = cursorToRule(cursor);
        cursor.close();
        return newRules;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void deleteItem(Deck deck) { //TODO
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        database.delete(DeckSQLiteHelper.TABLE_DECK,
                DeckSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Deck> getAllItems() {
        List<Deck> items = new ArrayList<Deck>();
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECK,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck rule = cursorToRule(cursor);
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
    public Deck cursorToRule(Cursor cursor) {
        Deck rule = new Deck();
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