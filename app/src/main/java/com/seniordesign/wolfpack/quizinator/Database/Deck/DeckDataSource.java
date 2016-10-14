package com.seniordesign.wolfpack.quizinator.Database.Deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;

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
            DeckSQLiteHelper.COLUMN_DECKNAME,
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
    public void deleteDeck(Deck deck) {
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        database.delete(DeckSQLiteHelper.TABLE_DECKS,
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
        cards = gson.fromJson(json, List.class);
        deck.setCards(cards);

        return deck;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
