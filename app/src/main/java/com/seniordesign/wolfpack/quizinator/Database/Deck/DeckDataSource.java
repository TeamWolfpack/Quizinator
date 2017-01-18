package com.seniordesign.wolfpack.quizinator.Database.Deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation.CdrSQLiteHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @creation 10/4/2016.
 */
public class DeckDataSource {

    // Database fields
    private SQLiteDatabase deckDatabase;
    private DeckSQLiteHelper deckHelper;
    private SQLiteDatabase cdrDatabase;
    private CdrSQLiteHelper cdrHelper;
    private String[] allColumns = {
            DeckSQLiteHelper.COLUMN_ID,
            DeckSQLiteHelper.COLUMN_DECKNAME,
            DeckSQLiteHelper.COLUMN_CATEGORY,
            DeckSQLiteHelper.COLUMN_SUBJECT,
            DeckSQLiteHelper.COLUMN_DUPLICATECARDS,
            DeckSQLiteHelper.COLUMN_OWNER
    };

    public DeckDataSource(Context context) {
        deckHelper = new DeckSQLiteHelper(context);
        cdrHelper = new CdrSQLiteHelper(context);
    }

    public boolean open() throws SQLException {
        deckDatabase = deckHelper.getWritableDatabase();
        cdrDatabase = cdrHelper.getWritableDatabase(); //TODO may need to just be cdrHelper.open()
        return true;
    }

    public boolean close() {
        deckHelper.close();
        cdrHelper.close();
        return true;
    }

    public SQLiteDatabase getDeckDatabase(){
        return deckDatabase;
    }

    public DeckSQLiteHelper getSQLiteHelper() {
        return deckHelper;
    }

    public Deck createDeck(String deckName, String category, String subject,
                           boolean duplicateCards, String owner, List<Card> cards) {
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deckName);
        values.put(DeckSQLiteHelper.COLUMN_CATEGORY, category);
        values.put(DeckSQLiteHelper.COLUMN_SUBJECT, subject);
        values.put(DeckSQLiteHelper.COLUMN_DUPLICATECARDS, String.valueOf(duplicateCards));
        values.put(DeckSQLiteHelper.COLUMN_OWNER, owner);
        long insertId = deckDatabase.insert(DeckSQLiteHelper.TABLE_DECKS,
                null, values);

        Cursor cursor = deckDatabase.query(DeckSQLiteHelper.TABLE_DECKS,
                allColumns, DeckSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Deck newDeck = cursorToDeck(cursor);
        cursor.close();
        setCardDeckRelation(insertId, cards);
        newDeck.setCards(cards);
        return newDeck;
    }

    private void setCardDeckRelation(long deckId, List<Card> cards) {
        ContentValues values;
        for (Card card : cards) {
            values = new ContentValues();
            values.put(CdrSQLiteHelper.COLUMN_FKCARD, card.getId());
            values.put(CdrSQLiteHelper.COLUMN_FKDECK, deckId);

            cdrDatabase.insert(CdrSQLiteHelper.TABLE_CDRELATIONS,
                    null, values);
        }
    }

    public int deleteDeck(Deck deck) {
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        cdrDatabase.delete(CdrSQLiteHelper.TABLE_CDRELATIONS,
                CdrSQLiteHelper.COLUMN_FKDECK + " = " + id, null);
        return deckDatabase.delete(DeckSQLiteHelper.TABLE_DECKS,
                DeckSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = deckDatabase.query(DeckSQLiteHelper.TABLE_DECKS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck deck = cursorToDeck(cursor);
            decks.add(deck);
            cursor.moveToNext();
        }
        cursor.close();
        return decks;
    }

    public Deck getDeckWithId(long id) {
        Cursor cursor = deckDatabase.query(DeckSQLiteHelper.TABLE_DECKS,
                allColumns, DeckSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();
        cdrDatabase.rawQuery()
        cursor = cdrDatabase.query(DeckSQLiteHelper.TABLE_DECKS,
                allColumns, DeckSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        return deck;
    }

    private Deck cursorToDeck(Cursor cursor) {
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

    public String[] getAllColumns(){
        return allColumns;
    }

    public int updateDeck(Deck deck){
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deck.getDeckName());

        Gson gson = new Gson();
        String cardsStr = gson.toJson(deck.getCards());
        values.put(DeckSQLiteHelper.COLUMN_CARDS, cardsStr);

        String where = DeckSQLiteHelper.COLUMN_ID + " = " + deck.getId();
        return deckDatabase.update(DeckSQLiteHelper.TABLE_DECKS, values, where, null);
    }
}
