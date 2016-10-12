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
            DeckSQLiteHelper.COLUMN_ID,
            DeckSQLiteHelper.COLUMN_DECK_NAME,
            DeckSQLiteHelper.COLUMN_CARDS
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
    public Deck createDeck(double weight, long date,
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
    public void deleteDeck(Deck deck) { //TODO
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        database.delete(DeckSQLiteHelper.TABLE_DECK,
                DeckSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<Deck>();
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECK,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck deck = cursorToDeck(cursor);
            decks.add(deck);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return decks;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Deck cursorToDeck(Cursor cursor) {
        Deck deck = new Deck();
//        rule.setId(cursor.getLong(0));//id
//        rule.setFishType(cursor.getString(1));//fishType
//        rule.setWeight(cursor.getDouble(2));//weight
//        rule.setLength(cursor.getDouble(3));//length
//        rule.setDate(cursor.getLong(4));//date
//        rule.setLocation(cursor.getString(5));//location
        return deck;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
