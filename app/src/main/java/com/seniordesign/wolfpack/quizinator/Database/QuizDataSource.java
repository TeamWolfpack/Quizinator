package com.seniordesign.wolfpack.quizinator.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seniordesign.wolfpack.quizinator.Constants;
import com.seniordesign.wolfpack.quizinator.Database.Card.Card;
import com.seniordesign.wolfpack.quizinator.Database.Card.CardSQLiteHelper;
import com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation.CardDeckRelation;
import com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation.CdrSQLiteHelper;
import com.seniordesign.wolfpack.quizinator.Database.Deck.Deck;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckSQLiteHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuizDataSource {

    // Database fields
    private SQLiteDatabase database;
    private QuizSQLiteHelper dbHelper;

    private String[] cardAllColumns = {
            QuizSQLiteHelper.CARD_COLUMN_ID,
            QuizSQLiteHelper.CARD_COLUMN_CARDTYPE,
            QuizSQLiteHelper.CARD_COLUMN_QUESTION,
            QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER,
            QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS,
            QuizSQLiteHelper.CARD_COLUMN_POINTS,
            QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED
    };

    private String[] deckAllColumns = {
            QuizSQLiteHelper.DECK_COLUMN_ID,
            QuizSQLiteHelper.DECK_COLUMN_DECKNAME,
            QuizSQLiteHelper.DECK_COLUMN_CATEGORY,
            QuizSQLiteHelper.DECK_COLUMN_SUBJECT,
            QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS,
            QuizSQLiteHelper.DECK_COLUMN_OWNER
    };

    private String[] cdrelationsAllColumns = {
            QuizSQLiteHelper.CDRELATIONS_COLUMN_ID,
            QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD,
            QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK
    };

    public QuizDataSource(Context context) {
        dbHelper = new QuizSQLiteHelper(context);
    }

    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return database.isOpen();
    }

    public boolean close() {
        dbHelper.close();
        return true;
    }

    public SQLiteDatabase getDatabase(){
        return database;
    }

    public QuizSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /************************ CARD METHODS START *******************************/
    public Card createCard(String cardType, String question, String correctAnswer,
                           String[] possibleCorrectAnswers, int points,
                           String moderatorNeeded) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, cardType);
        values.put(QuizSQLiteHelper.CARD_COLUMN_QUESTION, question);
        values.put(QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, correctAnswer);

        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(possibleCorrectAnswers);
        values.put(QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);

        values.put(QuizSQLiteHelper.CARD_COLUMN_POINTS, points);
        values.put(QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, moderatorNeeded);
        long insertId = database.insert(QuizSQLiteHelper.TABLE_CARDS,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, QuizSQLiteHelper.CARD_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Card newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

    public Card createCard(Card card) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, card.getCardType());
        values.put(QuizSQLiteHelper.CARD_COLUMN_QUESTION, card.getQuestion());
        values.put(QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, card.getCorrectAnswer());

        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(card.getPossibleAnswers());
        values.put(QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);

        values.put(QuizSQLiteHelper.CARD_COLUMN_POINTS, card.getPoints());
        values.put(QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
        long insertId = database.insert(QuizSQLiteHelper.TABLE_CARDS,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, QuizSQLiteHelper.CARD_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Card newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

    public int deleteCard(Card card) {
        long id = card.getId();
        System.out.println("Deleted card: " + card.toString());
        return database.delete(QuizSQLiteHelper.TABLE_CARDS,
                QuizSQLiteHelper.CARD_COLUMN_ID + " = " + id, null);
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<Card>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, null, null, null, null, null);
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
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, buildCardTypeWhereClause(cardTypes), null, null, null, null);
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

    private String buildCardTypeWhereClause(List<String> cardTypes) {
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

    public Card cursorToCard(Cursor cursor) {
        Card card = new Card();
        card.setId(cursor.getLong(0));
        card.setCardType(Constants.CARD_TYPES.values()[cursor.getInt(1)]);
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

    public String[] getCardAllColumns(){
        return cardAllColumns;
    }

    public int updateCard(Card card){
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, card.getCardType());
        values.put(QuizSQLiteHelper.CARD_COLUMN_QUESTION, card.getQuestion());
        values.put(QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, card.getCorrectAnswer());

        Gson gson = new Gson();
        String possibleAnswerStr = gson.toJson(card.getPossibleAnswers());
        values.put(QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, possibleAnswerStr);

        values.put(QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
        values.put(QuizSQLiteHelper.CARD_COLUMN_POINTS, card.getPoints());

        String where = QuizSQLiteHelper.CARD_COLUMN_ID + " = " + card.getId();
        return database.update(QuizSQLiteHelper.TABLE_CARDS, values, where, null);
    }

    /************************ CARD METHODS END *******************************/

    /************************ DECK METHODS START *******************************/
    public Deck createDeck(String deckName, String category, String subject,
                           boolean duplicateCards, String owner, List<Card> cards) {
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deckName);
        values.put(DeckSQLiteHelper.COLUMN_CATEGORY, category);
        values.put(DeckSQLiteHelper.COLUMN_SUBJECT, subject);
        values.put(DeckSQLiteHelper.COLUMN_DUPLICATECARDS, String.valueOf(duplicateCards));
        values.put(DeckSQLiteHelper.COLUMN_OWNER, owner);
        long insertId = database.insert(DeckSQLiteHelper.TABLE_DECKS,
                null, values);

        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
                deckAllColumns, DeckSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Deck newDeck = cursorToDeck(cursor);
        cursor.close();
//        setCardDeckRelation(insertId, cards); //TODO
        newDeck.setCards(cards);
        return newDeck;
    }

    public int deleteDeck(Deck deck) {
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        database.delete(CdrSQLiteHelper.TABLE_CDRELATIONS,
                CdrSQLiteHelper.COLUMN_FKDECK + " = " + id, null);
        return database.delete(DeckSQLiteHelper.TABLE_DECKS,
                DeckSQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
                deckAllColumns, null, null, null, null, null);
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
        Cursor cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
                deckAllColumns, DeckSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();
        //cdrDatabase.rawQuery()
        cursor = database.query(DeckSQLiteHelper.TABLE_DECKS,
                deckAllColumns, DeckSQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
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

    public String[] getDeckAllColumns(){
        return deckAllColumns;
    }

    public int updateDeck(Deck deck){
        ContentValues values = new ContentValues();
        values.put(DeckSQLiteHelper.COLUMN_DECKNAME, deck.getDeckName());

        Gson gson = new Gson();
        String cardsStr = gson.toJson(deck.getCards());
        //values.put(DeckSQLiteHelper.COLUMN_CARDS, cardsStr);

        String where = DeckSQLiteHelper.COLUMN_ID + " = " + deck.getId();
        return database.update(DeckSQLiteHelper.TABLE_DECKS, values, where, null);
    }
    /************************ DECK METHODS END *******************************/

    /************************ CARDDECKRELATION METHODS START *******************************/
    public CardDeckRelation createCardDeckRelation(long fkCard, long fkDeck) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD, fkCard);
        values.put(QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK, fkDeck);

        long insertId = database.insert(QuizSQLiteHelper.TABLE_CDRELATIONS,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdrelationsAllColumns, QuizSQLiteHelper.CDRELATIONS_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        CardDeckRelation newCardDeckRelation = cursorToCardDeckRelation(cursor);
        cursor.close();
        return newCardDeckRelation;
    }

    public CardDeckRelation createCardDeckRelation(CardDeckRelation cdRelation) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD, cdRelation.getFkCard());
        values.put(QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK, cdRelation.getFkDeck());

        long insertId = database.insert(QuizSQLiteHelper.TABLE_CDRELATIONS,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdrelationsAllColumns, QuizSQLiteHelper.CDRELATIONS_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        CardDeckRelation newCardDeckRelation = cursorToCardDeckRelation(cursor);
        cursor.close();
        return newCardDeckRelation;
    }

    public int deleteCardDeckRelation(CardDeckRelation cdRelation) {
        long id = cdRelation.getId();
        System.out.println("Deleted card: " + cdRelation.toString());
        return database.delete(QuizSQLiteHelper.TABLE_CDRELATIONS,
                QuizSQLiteHelper.CDRELATIONS_COLUMN_ID + " = " + id, null);
    }

    public List<CardDeckRelation> getAllCardDeckRelations() {
        List<CardDeckRelation> cdRelations = new ArrayList<>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdrelationsAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CardDeckRelation cdRelation = cursorToCardDeckRelation(cursor);
            cdRelations.add(cdRelation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return cdRelations;
    }

    public CardDeckRelation cursorToCardDeckRelation(Cursor cursor) {
        CardDeckRelation cdRelation = new CardDeckRelation();
        cdRelation.setId(cursor.getLong(0));
        cdRelation.setFkCard(cursor.getLong(1));
        cdRelation.setFkDeck(cursor.getLong(2));
        return cdRelation;
    }

    public String[] getCdrelationAllColumns(){
        return cdrelationsAllColumns;
    }
    /************************ CARDDECKRELATION METHODS END *******************************/
}
