package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @creation    10/4/2016.
 */
public class CardDataSource {

    // Database fields
    private SQLiteDatabase database;
    private CardSQLiteHelper dbHelper;
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
    public CardDataSource(Context context) {
        dbHelper = new CardSQLiteHelper(context);
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
    public CardSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Card createCard(double weight, long date,
                            String location) {
        ContentValues values = new ContentValues();
//        values.put(CardSQLiteHelper.COLUMN_FISHTYPE, "Fish");
//        values.put(CardSQLiteHelper.COLUMN_WEIGHT, weight);
//        values.put(CardSQLiteHelper.COLUMN_LENGTH, 0.0);
//        values.put(CardSQLiteHelper.COLUMN_DATE, date);
//        values.put(CardSQLiteHelper.COLUMN_LOCATION, location);
        long insertId = database.insert(CardSQLiteHelper.TABLE_CARD,
                null, values);
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARD,
                allColumns, CardSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Card newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public void deleteItem(Card card) { //TODO
        long id = card.getId();
        System.out.println("Deleted item: " + card.toString());
        database.delete(CardSQLiteHelper.TABLE_CARD,
                CardSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Card> getAllItems() {
        List<Card> items = new ArrayList<Card>();
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARD,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = cursorToCard(cursor);
            items.add(card);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Card cursorToCard(Cursor cursor) {
//        Card card = new Card();
//        rule.setId(cursor.getLong(0));//id
//        rule.setFishType(cursor.getString(1));//fishType
//        rule.setWeight(cursor.getDouble(2));//weight
//        rule.setLength(cursor.getDouble(3));//length
//        rule.setDate(cursor.getLong(4));//date
//        rule.setLocation(cursor.getString(5));//location
//        return card;
        return null;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
