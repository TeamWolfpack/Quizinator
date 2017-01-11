package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

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
            CardSQLiteHelper.COLUMN_ID,
            CardSQLiteHelper.COLUMN_CARDTYPE,
            CardSQLiteHelper.COLUMN_QUESTION,
            CardSQLiteHelper.COLUMN_CORRECTANSWER,
            CardSQLiteHelper.COLUMN_POSSIBLEANSWERS,
            CardSQLiteHelper.COLUMN_POINTS,
            CardSQLiteHelper.COLUMN_MODERATORNEEDED
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
    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return database.isOpen();
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
    public CardSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public Card createCard(String cardType, String question, String correctAnswer,
                           String[] possibleCorrectAnswers, int points,
                           String moderatorNeeded) {
        ContentValues values = new ContentValues();
        values.put(CardSQLiteHelper.COLUMN_CARDTYPE, cardType);
        values.put(CardSQLiteHelper.COLUMN_QUESTION, question);
        values.put(CardSQLiteHelper.COLUMN_CORRECTANSWER, correctAnswer);

        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(possibleCorrectAnswers);
        values.put(CardSQLiteHelper.COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);

        values.put(CardSQLiteHelper.COLUMN_POINTS, points);
        values.put(CardSQLiteHelper.COLUMN_MODERATORNEEDED, moderatorNeeded);
        long insertId = database.insert(CardSQLiteHelper.TABLE_CARDS,
                null, values);
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARDS,
                allColumns, CardSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Card newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

    public Card createCard(Card card) {
        ContentValues values = new ContentValues();
        values.put(CardSQLiteHelper.COLUMN_CARDTYPE, card.getCardType());
        values.put(CardSQLiteHelper.COLUMN_QUESTION, card.getQuestion());
        values.put(CardSQLiteHelper.COLUMN_CORRECTANSWER, card.getCorrectAnswer());

        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(card.getPossibleAnswers());
        values.put(CardSQLiteHelper.COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);

        values.put(CardSQLiteHelper.COLUMN_POINTS, card.getPoints());
        values.put(CardSQLiteHelper.COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
        long insertId = database.insert(CardSQLiteHelper.TABLE_CARDS,
                null, values);
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARDS,
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
    public int deleteCard(Card card) {
        long id = card.getId();
        System.out.println("Deleted card: " + card.toString());
        return database.delete(CardSQLiteHelper.TABLE_CARDS,
                CardSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<Card>();
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARDS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = cursorToCard(cursor);
            cards.add(card);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return cards;
    }

    // TODO will update later as more filtering options are created
    // TODO can also replace getAllCards just by passing in nulls but can be looked into later
    public List<Card> filterCards(List<String> cardTypes) {
        List<Card> cards = new ArrayList<Card>();
        Cursor cursor = database.query(CardSQLiteHelper.TABLE_CARDS,
                allColumns, buildWhereClause(cardTypes), null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = cursorToCard(cursor);
            cards.add(card);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        Log.d("DATABASE", "Starting log of cards: size = " + cards.size());
        for(int i = 0; i < cards.size(); i++){
            Log.d("DATABASE", i + " : " + cards.get(i).toString());
        }

        return cards;
    }

    /*
     * Need to check to make sure that the cardType string is shortFormatted
     * before building whereClause.
     */
    private List<String> checkCardTypeForm(List<String> cardTypes){

        return cardTypes;
    }

    private String buildWhereClause(List<String> cardTypes) {
        if (cardTypes == null || cardTypes.size() == 0) {
            return null;
        }
        StringBuilder whereClause = new StringBuilder();
        int i = 0;
        while (cardTypes.size() != i + 1) {
            whereClause.append(CardSQLiteHelper.COLUMN_CARDTYPE)
                    .append("=\'")
                    .append(cardTypes.get(i))
                    .append("\' OR ");
            i++;
        }
        whereClause.append(CardSQLiteHelper.COLUMN_CARDTYPE)
                .append("=\'")
                .append(cardTypes.get(i))
                .append("\'");
        Log.d("DATABASE", "whereClause: " + whereClause.toString());
        return whereClause.toString();
    }


    /*
     * @author  chuna (10/4/2016)
     */
    public Card cursorToCard(Cursor cursor) {
        Card card = new Card();
        card.setId(cursor.getLong(0));
        card.setCardType(cursor.getString(1));
        card.setQuestion(cursor.getString(2));
        card.setCorrectAnswer(cursor.getString(3));

        //TODO make sure this works and we can convert back into String Array
        String[] answers;
        Gson gson = new Gson();
        String json = cursor.getString(4);
        answers = gson.fromJson(json, String[].class);
        card.setPossibleAnswers(answers);

        card.setPoints(cursor.getInt(5));
        card.setModeratorNeeded(cursor.getString(6));
        return card;
    }

    /*
     * @author  chuna (10/4/2016)
     */
    public String[] getAllColumns(){
        return allColumns;
    }

    /*
     * @author  chuna (10/16/2016)
     */
    public int updateCard(Card card){
        ContentValues values = new ContentValues();
        values.put(CardSQLiteHelper.COLUMN_CARDTYPE, card.getCardType());
        values.put(CardSQLiteHelper.COLUMN_QUESTION, card.getQuestion());
        values.put(CardSQLiteHelper.COLUMN_CORRECTANSWER, card.getCorrectAnswer());

        Gson gson = new Gson();
        String possibleAnswerStr = gson.toJson(card.getPossibleAnswers());
        values.put(CardSQLiteHelper.COLUMN_POSSIBLEANSWERS, possibleAnswerStr);

        values.put(CardSQLiteHelper.COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
        values.put(CardSQLiteHelper.COLUMN_POINTS, card.getPoints());

        String where = CardSQLiteHelper.COLUMN_ID + " = " + card.getId();
        return database.update(CardSQLiteHelper.TABLE_CARDS, values, where, null);
    }
}
