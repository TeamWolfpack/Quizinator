package com.seniordesign.wolfpack.quizinator.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

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

    private String[] cdRelationsAllColumns = {
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
    public Card createCard(int cardType, String question, String correctAnswer,
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
        return createCard(card.getCardType(), card.getQuestion(), card.getCorrectAnswer(),
                card.getPossibleAnswers(), card.getPoints(), card.getModeratorNeeded());
//        ContentValues values = new ContentValues();
//        values.put(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, card.getCardType());
//        values.put(QuizSQLiteHelper.CARD_COLUMN_QUESTION, card.getQuestion());
//        values.put(QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, card.getCorrectAnswer());
//
//        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
//        Gson gson = new Gson();
//        String stringPossibleAnswers = gson.toJson(card.getPossibleAnswers());
//        values.put(QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);
//
//        values.put(QuizSQLiteHelper.CARD_COLUMN_POINTS, card.getPoints());
//        values.put(QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
//        long insertId = database.insert(QuizSQLiteHelper.TABLE_CARDS,
//                null, values);
//        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CARDS,
//                cardAllColumns, QuizSQLiteHelper.CARD_COLUMN_ID
//                        + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        Card newCard = cursorToCard(cursor);
//        cursor.close();
//        return newCard;
    }

    public int deleteCard(Card card) {
        long id = card.getId();
        System.out.println("Deleted card: " + card.toString());
        deleteCardDeckRelationByCardId(id);
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
    public List<Card> filterCards(List<Constants.CARD_TYPES> cardTypes) {
        List<Card> cards = new ArrayList<>();
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
            Log.d("DATABASE", i + " : " + cards.get(i));
        }

        return cards;
    }

    private String buildCardTypeWhereClause(List<Constants.CARD_TYPES> cardTypes) {
        if (cardTypes == null || cardTypes.size() == 0) {
            return null;
        }
        StringBuilder whereClause = new StringBuilder();
        int i = 0;
        while (cardTypes.size() != i + 1) {
            whereClause.append(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE)
                    .append("=\'")
                    .append(cardTypes.get(i).ordinal())
                    .append("\' OR ");
            i++;
        }
        whereClause.append(QuizSQLiteHelper.CARD_COLUMN_CARDTYPE)
                .append("=\'")
                .append(cardTypes.get(i).ordinal())
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
        values.put(QuizSQLiteHelper.DECK_COLUMN_DECKNAME, deckName);
        values.put(QuizSQLiteHelper.DECK_COLUMN_CATEGORY, category);
        values.put(QuizSQLiteHelper.DECK_COLUMN_SUBJECT, subject);
        values.put(QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS, String.valueOf(duplicateCards));
        values.put(QuizSQLiteHelper.DECK_COLUMN_OWNER, owner);
        long insertId = database.insert(QuizSQLiteHelper.TABLE_DECKS,
                null, values);

        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, QuizSQLiteHelper.DECK_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Deck newDeck = cursorToDeck(cursor);
        cursor.close();
        addCardDeckRelation(insertId, cards);
        newDeck.setCards(cards);
        return newDeck;
    }

    public Deck createDeck(Deck deck) {
        return createDeck(deck.getDeckName(), deck.getCategory(), deck.getSubject(),
                deck.isDuplicateCards(), deck.getOwner(), deck.getCards());
    }

    public int deleteDeck(Deck deck) {
        long id = deck.getId();
        System.out.println("Deleted item: " + deck.toString());
        deleteCardDeckRelationByDeckId(id);
        return database.delete(QuizSQLiteHelper.TABLE_DECKS,
                QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null);
    }

    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck deck = cursorToDeck(cursor);
            deck.setCards(getAllCardsInDeck(deck.getId()));
            decks.add(deck);
            cursor.moveToNext();
        }
        cursor.close();
        return decks;
    }

    public Deck getDeckWithId(long id) {
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        cursor.close();
        cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        deck.setCards(getAllCardsInDeck(id));
        return deck;
    }

    private Deck cursorToDeck(Cursor cursor) {
        Deck deck = new Deck();
        deck.setId(cursor.getLong(0));
        deck.setDeckName(cursor.getString(1));
        deck.setCategory(cursor.getString(2));
        deck.setSubject(cursor.getString(3));
        deck.setDuplicateCards(cursor.getString(4));
        deck.setOwner(cursor.getString(5));
        return deck;
    }

    public String[] getDeckAllColumns(){
        return deckAllColumns;
    }

    public int updateDeck(Deck deck){
        updateCardDeckRelation(deck);
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.DECK_COLUMN_DECKNAME, deck.getDeckName());
        values.put(QuizSQLiteHelper.DECK_COLUMN_CATEGORY, deck.getCategory());
        values.put(QuizSQLiteHelper.DECK_COLUMN_SUBJECT, deck.getSubject());
        values.put(QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS, String.valueOf(deck.isDuplicateCards()));
        values.put(QuizSQLiteHelper.DECK_COLUMN_OWNER, deck.getOwner());

        String where = QuizSQLiteHelper.DECK_COLUMN_ID + " = " + deck.getId();
        return database.update(QuizSQLiteHelper.TABLE_DECKS, values, where, null);
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
                cdRelationsAllColumns, QuizSQLiteHelper.CDRELATIONS_COLUMN_ID
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
                cdRelationsAllColumns, QuizSQLiteHelper.CDRELATIONS_COLUMN_ID
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

    public int deleteCardDeckRelationByCardId(long fkCard) {
        return database.delete(QuizSQLiteHelper.TABLE_CDRELATIONS,
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD + " = " + fkCard, null);
    }

    public int deleteCardDeckRelationByDeckId(long fkDeck) {
        return database.delete(QuizSQLiteHelper.TABLE_CDRELATIONS,
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + " = " + fkDeck, null);
    }

    private List<Card> getAllCardsInDeck(long deckId) {
        ArrayList<Card> cards = new ArrayList<>();
        StringBuilder cardColumns = new StringBuilder();
        for (String cardColumn : cardAllColumns) {
            cardColumns.append("c.").append(cardColumn).append(",");
        }
        cardColumns.deleteCharAt(cardColumns.length()-1);
        StringBuilder query = new StringBuilder()
                .append("SELECT ").append(cardColumns.toString())
                .append(" FROM " + QuizSQLiteHelper.TABLE_CDRELATIONS + " cdr")
                .append(" INNER JOIN " + QuizSQLiteHelper.TABLE_CARDS + " c")
                .append(" ON cdr." + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD + "=c." + QuizSQLiteHelper.CARD_COLUMN_ID)
                .append(" INNER JOIN " + QuizSQLiteHelper.TABLE_DECKS + " d")
                .append(" ON cdr." + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + "=d." + QuizSQLiteHelper.DECK_COLUMN_ID)
                .append(" WHERE cdr." + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + "=\'").append(deckId).append("\'");
        Log.d("FUCKINGQUERY", query.toString());
        Cursor cursor = database.rawQuery(query.toString(), null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Card card = cursorToCard(cursor);
            cards.add(card);
            cursor.moveToNext();
        }
        return cards;
    }

    private void addCardDeckRelation(long deckId, List<Card> cards) {
        for (Card card : cards) {
            createCardDeckRelation(card.getId(), deckId);
        }
    }

    public List<CardDeckRelation> getAllCardDeckRelations() {
        List<CardDeckRelation> cdRelations = new ArrayList<>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdRelationsAllColumns, null, null, null, null, null);
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
        return cdRelationsAllColumns;
    }

    public void updateCardDeckRelation(Deck deck) {
        deleteCardDeckRelationByDeckId(deck.getId());
        addCardDeckRelation(deck.getId(), deck.getCards());
    }
    /************************ CARDDECKRELATION METHODS END *******************************/
}
