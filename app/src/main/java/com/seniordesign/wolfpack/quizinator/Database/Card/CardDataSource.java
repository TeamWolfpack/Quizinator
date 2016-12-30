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

    private String buildWhereClause(List<String> cardTypes) {
        if (cardTypes == null || cardTypes.size() == 0) {
            return null;
        }
        StringBuilder whereClause = new StringBuilder();
        int i = 0;
        while (cardTypes.size() != i + 1) {
            whereClause.append(CardSQLiteHelper.COLUMN_CARDTYPE)
                    .append("=\"")
                    .append(cardTypes.get(i))
                    .append("\" OR ");
            i++;
        }
        whereClause.append(CardSQLiteHelper.COLUMN_CARDTYPE)
                .append("=\"")
                .append(cardTypes.get(i))
                .append("\"");
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

//    void setDefaultCardSet() {
//        Card[] cards = new Card[10];
//        cards[0] = new Card();
//        cards[0].setQuestion("1+1 = ?");
//        cards[0].setCorrectAnswer("2");
//        String[] answerArea = {"1","2","3","4"};
//        cards[0].setPossibleAnswers(answerArea);
//        cards[0].setCardType(Constants.SHORT_MULTIPLE_CHOICE);
//        cards[0].setPoints(1);
//        cards[0].setModeratorNeeded("False");
//        cards[1] = new Card();
//        cards[1].setQuestion("1*2 = 0");
//        cards[1].setCorrectAnswer("False");
//        cards[1].setCardType(Constants.SHORT_TRUE_FALSE);
//        String[] answerAreaTF = {"True", "False"};
//        cards[1].setPossibleAnswers(answerAreaTF);
//        cards[1].setPoints(1);
//        cards[1].setModeratorNeeded("False");
//        cards[2] = new Card();
//        cards[2].setQuestion("4*5 = 20");
//        cards[2].setCorrectAnswer("True");
//        cards[2].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[2].setPossibleAnswers(answerAreaTF);
//        cards[2].setPoints(1);
//        cards[2].setModeratorNeeded("False");
//        cards[3] = new Card();
//        cards[3].setQuestion("20*10 = 100");
//        cards[3].setCorrectAnswer("False");
//        cards[3].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[3].setPossibleAnswers(answerAreaTF);
//        cards[3].setPoints(1);
//        cards[3].setModeratorNeeded("False");
//        cards[4] = new Card();
//        cards[4].setQuestion("10*91 = 901");
//        cards[4].setCorrectAnswer("False");
//        cards[4].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[4].setPossibleAnswers(answerAreaTF);
//        cards[4].setPoints(1);
//        cards[4].setModeratorNeeded("False");
//        cards[5] = new Card();
//        cards[5].setQuestion("100^2 = 10000");
//        cards[5].setCorrectAnswer("True");
//        cards[5].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[5].setPossibleAnswers(answerAreaTF);
//        cards[5].setPoints(1);
//        cards[5].setModeratorNeeded("False");
//        cards[6] = new Card();
//        cards[6].setQuestion("10*102 = 1002");
//        cards[6].setCorrectAnswer("False");
//        cards[6].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[6].setPossibleAnswers(answerAreaTF);
//        cards[6].setPoints(1);
//        cards[6].setModeratorNeeded("False");
//        cards[7] = new Card();
//        cards[7].setQuestion("8/2 = 4");
//        cards[7].setCorrectAnswer("True");
//        cards[7].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[7].setPossibleAnswers(answerAreaTF);
//        cards[7].setPoints(1);
//        cards[7].setModeratorNeeded("False");
//        cards[8] = new Card();
//        cards[8].setQuestion("120/4 = 30");
//        cards[8].setCorrectAnswer("True");
//        cards[8].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[8].setPossibleAnswers(answerAreaTF);
//        cards[8].setPoints(1);
//        cards[8].setModeratorNeeded("False");
//        cards[9] = new Card();
//        cards[9].setQuestion("6*7 = 41");
//        cards[9].setCorrectAnswer("False");
//        cards[9].setCardType(Constants.SHORT_TRUE_FALSE);
//        cards[9].setPossibleAnswers(answerAreaTF);
//        cards[9].setPoints(1);
//        cards[9].setModeratorNeeded("False");
//
//        this.open();
//        for (Card card : cards) {
//            this.createCard(card);
//        }
//        this.close();
//    }
}
