package com.seniordesign.wolfpack.quizinator.Database.Deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @creation 10/4/2016.
 */
public class DeckDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DeckSQLiteHelper dbHelper;
    private String[] allColumns = {
            DeckSQLiteHelper.COLUMN_ID,
            DeckSQLiteHelper.COLUMN_DECKNAME,
            DeckSQLiteHelper.COLUMN_CATEGORY,
            DeckSQLiteHelper.COLUMN_SUBJECT,
            DeckSQLiteHelper.COLUMN_DUPLICATECARDS,
            DeckSQLiteHelper.COLUMN_OWNER
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
    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return true;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public boolean close() {
        dbHelper.close();
        return true;
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
    public Deck createDeck(String deckName, List<Card> cards) {
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deckName);

        // TODO make sure this works like in CardDataSource
        Gson gson = new Gson();
        String cardsStr = gson.toJson(cards);
        values.put(DeckSQLiteHelper.COLUMN_CARDS, cardsStr);

        long insertId = database.insert(DeckSQLiteHelper.TABLE_DECKS,
                null, values);
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
                allColumns, DeckSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Deck newDeck = cursorToDeck(cursor);
        cursor.close();
        return newDeck;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public int deleteDeck(Deck deck) {
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        return database.delete(DeckSQLiteHelper.TABLE_DECKS,
                DeckSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<Deck>();
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
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
        deck.setId(cursor.getLong(0));
        deck.setDeckName(cursor.getString(1));

        //TODO make sure this works like in CardDatasource
        List<Card> cards;
        Gson gson = new Gson();
        String json = cursor.getString(2);

        Type listType = new TypeToken<List<Card>>(){}.getType();
        cards = new Gson().fromJson(json, listType);
        deck.setCards(cards);

        return deck;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public String[] getAllColumns(){
        return allColumns;
    }

    /*
     * @author  chuna (10//2016)
     */
    public int updateDeck(Deck deck){
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deck.getDeckName());

        Gson gson = new Gson();
        String cardsStr = gson.toJson(deck.getCards());
        values.put(DeckSQLiteHelper.COLUMN_CARDS, cardsStr);

        String where = DeckSQLiteHelper.COLUMN_ID + " = " + deck.getId();
        return database.update(DeckSQLiteHelper.TABLE_DECKS, values, where, null);
    }
}
