package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

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
            CardSQLiteHelper.COLUMN_CARD_TYPE,
            CardSQLiteHelper.COLUMN_QUESTION,
            CardSQLiteHelper.COLUMN_CORRECT_ANSWER,
            CardSQLiteHelper.COLUMN_POSSIBLE_ANSWERS,
            CardSQLiteHelper.COLUMN_POINTS,
            CardSQLiteHelper.COLUMN_MODERATOR_NEEDED
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
    public Card createCard(String cardType, String question, String correctAnswer,
                           String[] possibleCorrectAnswers, int points,
                           String moderatorNeeded) {
        ContentValues values = new ContentValues();
        values.put(CardSQLiteHelper.COLUMN_CARD_TYPE, cardType);
        values.put(CardSQLiteHelper.COLUMN_QUESTION, question);
        values.put(CardSQLiteHelper.COLUMN_CORRECT_ANSWER, correctAnswer);

        //TODO make sure this works
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(possibleCorrectAnswers);
        values.put(CardSQLiteHelper.COLUMN_POSSIBLE_ANSWERS, stringPossibleAnswers);

        values.put(CardSQLiteHelper.COLUMN_POINTS, points);
        values.put(CardSQLiteHelper.COLUMN_MODERATOR_NEEDED, moderatorNeeded);
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
    public void deleteCard(Card card) {
        long id = card.getId();
        System.out.println("Deleted card: " + card.toString());
        database.delete(CardSQLiteHelper.TABLE_CARDS,
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

    /*
     * @author  chuna (10/4/2016)
     */
    public Card cursorToCard(Cursor cursor) {
        String cardType = cursor.getString(1);
        Card card = new TFCard(); // Defaulting to a TFCard
        switch(cardType) {
            case "TF":
                card = new TFCard();
                break;
            case "MC":
                card = new MCCard();
                break;
            //TODO will implement later
//            case "FR":
//                card = new FRCard();
//                break;
//            case "VR":
//                card = new VRCard();
//                break;
        }
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

    public String[] getAllColumns(){
        return allColumns;
    }
}
