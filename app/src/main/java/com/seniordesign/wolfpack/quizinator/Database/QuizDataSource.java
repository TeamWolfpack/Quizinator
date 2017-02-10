package com.seniordesign.wolfpack.quizinator.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    private String[] highScoresAllColumns = {
            QuizSQLiteHelper.HIGHSCORES_COLUMN_ID,
            QuizSQLiteHelper.HIGHSCORES_COLUMN_DECKNAME,
            QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTTIME,
            QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTSCORE
    };

    private String[] rulesAllColumns = {
            QuizSQLiteHelper.RULES_COLUMN_ID,
            QuizSQLiteHelper.RULES_COLUMN_TIMELIMIT,
            QuizSQLiteHelper.RULES_COLUMN_CARDDISPLAYTIME,
            QuizSQLiteHelper.RULES_COLUMN_MAXCARDCOUNT,
            QuizSQLiteHelper.RULES_COLUMN_CARDTYPES,
            QuizSQLiteHelper.RULES_COLUMN_DECK_ID
    };

    private String[] settingsAllColumns = {
            QuizSQLiteHelper.SETTINGS_COLUMN_ID,
            QuizSQLiteHelper.SETTINGS_COLUMN_USERNAME,
            QuizSQLiteHelper.SETTINGS_COLUMN_NUMBEROFCONNECTIONS
    };

    public QuizDataSource(Context context) {
        dbHelper = new com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper(context);
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

    public com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /************************ CARD METHODS START *******************************/
    public Card createCard(int cardType, String question, String correctAnswer,
                           String[] possibleCorrectAnswers, int points,
                           String moderatorNeeded) {
        ContentValues values = new ContentValues();
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, cardType);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_QUESTION, question);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, correctAnswer);

        //TODO need to make sure that depending on card type, the correct number of possible answers is inputted... can be done here or in code calling this method
        Gson gson = new Gson();
        String stringPossibleAnswers = gson.toJson(possibleCorrectAnswers);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, stringPossibleAnswers);

        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_POINTS, points);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, moderatorNeeded);
        long insertId = database.insert(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS,
                null, values);
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_ID
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
    }

    public int deleteCard(Card card) {
        long id = card.getId();
        System.out.println("Deleted card: " + card.toString());
        deleteCardDeckRelationByCardId(id);
        return database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_ID + " = " + id, null);
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS,
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
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS,
                cardAllColumns, buildCardTypeWhereClause(null, cardTypes), null, null, null, null);
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
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CARDTYPE, card.getCardType());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_QUESTION, card.getQuestion());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CORRECTANSWER, card.getCorrectAnswer());

        Gson gson = new Gson();
        String possibleAnswerStr = gson.toJson(card.getPossibleAnswers());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_POSSIBLEANSWERS, possibleAnswerStr);

        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED, card.getModeratorNeeded());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_POINTS, card.getPoints());

        String where = com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_ID + " = " + card.getId();
        return database.update(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS, values, where, null);
    }

    /************************ CARD METHODS END *******************************/

    /************************ DECK METHODS START *******************************/
    public Deck createDeck(String deckName, String category, String subject,
                           boolean duplicateCards, String owner, List<Card> cards) {
        ContentValues values = new ContentValues();
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_DECKNAME, deckName);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_CATEGORY, category);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_SUBJECT, subject);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS, String.valueOf(duplicateCards));
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_OWNER, owner);
        long insertId = database.insert(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS,
                null, values);

        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_ID
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
        return database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null);
    }

    public List<Deck> getAllDecks() {
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS,
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
        return getFilteredDeck(id, null, true);
//        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
//                deckAllColumns, QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null, null, null, null);
//        cursor.moveToFirst();
//        Deck deck = cursorToDeck(cursor);
////        cursor.close();
////        cursor = database.query(QuizSQLiteHelper.TABLE_DECKS,
////                deckAllColumns, QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null, null, null, null);
////        cursor.moveToFirst();
//        deck.setCards(getAllCardsInDeck(id));
//        cursor.close();
//        return deck;
    }

    public Deck getFilteredDeck(long id, List<Constants.CARD_TYPES> cardTypes, boolean moderatorNeeded) {
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS,
                deckAllColumns, com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Deck deck = cursorToDeck(cursor);
        deck.setCards(getFilteredCardsInDeck(id, cardTypes, moderatorNeeded));
        cursor.close();
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
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_DECKNAME, deck.getDeckName());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_CATEGORY, deck.getCategory());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_SUBJECT, deck.getSubject());
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_DUPLICATECARDS, String.valueOf(deck.isDuplicateCards()));
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_OWNER, deck.getOwner());

        String where = com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_ID + " = " + deck.getId();
        return database.update(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS, values, where, null);
    }
    /************************ DECK METHODS END *******************************/

    /************************ CARDDECKRELATION METHODS START *******************************/
    public CardDeckRelation createCardDeckRelation(long fkCard, long fkDeck) {
        ContentValues values = new ContentValues();
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD, fkCard);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK, fkDeck);

        long insertId = database.insert(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
                null, values);
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdRelationsAllColumns, com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        CardDeckRelation newCardDeckRelation = cursorToCardDeckRelation(cursor);
        cursor.close();
        return newCardDeckRelation;
    }

    public CardDeckRelation createCardDeckRelation(CardDeckRelation cdRelation) {
        return createCardDeckRelation(cdRelation.getFkCard(), cdRelation.getFkDeck());
    }

    public int deleteCardDeckRelation(CardDeckRelation cdRelation) {
        long id = cdRelation.getId();
        System.out.println("Deleted card: " + cdRelation.toString());
        return database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_ID + " = " + id, null);
    }

    public int deleteCardDeckRelationByCardId(long fkCard) {
        return database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD + " = " + fkCard, null);
    }

    public int deleteCardDeckRelationByDeckId(long fkDeck) {
        return database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + " = " + fkDeck, null);
    }

    private List<Card> getAllCardsInDeck(long deckId) {
        return getFilteredCardsInDeck(deckId, null, true);
    }

    private List<Card> getFilteredCardsInDeck(long deckId, List<Constants.CARD_TYPES> cardTypes, boolean moderatorNeeded) {
        String cardTableName = "c";
        String deckTableName = "d";
        String cdrTableName = "cdr";

        ArrayList<Card> cards = new ArrayList<>();
        StringBuilder cardColumns = new StringBuilder();
        for (String cardColumn : cardAllColumns) {
            cardColumns.append(cardTableName).append(".").append(cardColumn).append(",");
        }
        cardColumns.deleteCharAt(cardColumns.length()-1);
        StringBuilder query = new StringBuilder()
                .append("SELECT ").append(cardColumns.toString())
                .append(" FROM " + com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS + " ").append(cdrTableName)
                .append(" INNER JOIN " + com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CARDS + " ").append(cardTableName)
                .append(" ON ").append(cdrTableName).append(".").append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD).append("=").append(cardTableName).append(".").append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_ID)
                .append(" INNER JOIN " + com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_DECKS + " ").append(deckTableName)
                .append(" ON ").append(cdrTableName).append(".").append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK).append("=").append(deckTableName).append(".").append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.DECK_COLUMN_ID)
                .append(" WHERE ").append(cdrTableName).append(".").append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK).append("=\'").append(deckId).append("\'");
        if(!(cardTypes == null || cardTypes.isEmpty()))
            query.append(" AND ").append(buildCardTypeWhereClause(cardTableName, cardTypes));
        if(!moderatorNeeded)
            query.append(" AND ").append(buildModeratorNeededWhereClasue(cardTableName, false));
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
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_CDRELATIONS,
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

    /************************ HIGHSCORE METHODS START *******************************/
    public HighScores createHighScore(String deckName, long time, int bestScore) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_DECKNAME, deckName);
        values.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTTIME, time);
        values.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTSCORE, bestScore);
        long insertId = database.insert(QuizSQLiteHelper.TABLE_HIGHSCORES,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_HIGHSCORES,
                highScoresAllColumns, QuizSQLiteHelper.HIGHSCORES_COLUMN_ID
                        + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        HighScores newHighScores = cursorToHighScore(cursor);
        cursor.close();
        return newHighScores;
    }

    public HighScores createHighScore(HighScores highScores) {
        return createHighScore(highScores.getDeckName(), highScores.getBestTime(), highScores.getBestScore());
    }

    public int updateHighScore(HighScores hs) {
        ContentValues cv = new ContentValues();
        cv.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_ID, hs.getId());
        cv.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_DECKNAME, hs.getDeckName());
        cv.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTTIME, hs.getBestTime());
        cv.put(QuizSQLiteHelper.HIGHSCORES_COLUMN_BESTSCORE, hs.getBestScore());
        String where = QuizSQLiteHelper.HIGHSCORES_COLUMN_ID + " = " + hs.getId();
        return database.update(QuizSQLiteHelper.TABLE_HIGHSCORES, cv, where, null);
    }

    public boolean deleteHighScore(HighScores scores) {
        long id = scores.getId();
        database.delete(QuizSQLiteHelper.TABLE_HIGHSCORES,
                QuizSQLiteHelper.HIGHSCORES_COLUMN_ID + " = " + id, null);
        return true;
    }

    public List<HighScores> getAllHighScores() {
        if (!database.isOpen()) {
            open();
        }

        List<HighScores> items = new ArrayList<HighScores>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_HIGHSCORES,
                highScoresAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HighScores rule = cursorToHighScore(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public HighScores cursorToHighScore(Cursor cursor) {
        HighScores scores = new HighScores();
        scores.setId(cursor.getLong(0));//id
        scores.setDeckName(cursor.getString(1));//deck name
        scores.setBestTime(cursor.getLong(2));//best time
        scores.setBestScore(cursor.getInt(3));//best score
        return scores;
    }

    public String[] getHighScoresAllColumns(){
        return highScoresAllColumns;
    }
    /************************ HIGHSCORE METHODS END *******************************/

    /************************ RULES METHODS START *******************************/
    public Rules createRule(int maxCardCount, long timeLimit,
                            long cardDisplayTime, String cardTypes, long deckId) {
        ContentValues values = new ContentValues();
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_TIMELIMIT, timeLimit);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_CARDDISPLAYTIME, cardDisplayTime);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_MAXCARDCOUNT, maxCardCount);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_CARDTYPES, cardTypes);
        values.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_DECK_ID, deckId);
        long insertId = database.insert(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_RULES,
                null, values);
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_RULES,
                rulesAllColumns, com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_ID
                        + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Rules newRules = cursorToRule(cursor);
        cursor.close();
        return newRules;
    }

    public Rules createRule(Rules rule){
        return createRule(rule.getMaxCardCount(), rule.getTimeLimit(),
                rule.getCardDisplayTime(), rule.getCardTypes(), rule.getDeckId());
    }

    public int updateRules(Rules r){
        ContentValues cv = new ContentValues();
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_ID, r.getId());
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_TIMELIMIT, r.getTimeLimit());
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_CARDDISPLAYTIME, r.getCardDisplayTime());
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_MAXCARDCOUNT, r.getMaxCardCount());
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_CARDTYPES, r.getCardTypes());
        cv.put(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_DECK_ID, r.getDeckId());
        String where = com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_ID + " = " + r.getId();
        return database.update(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_RULES, cv, where, null);
    }

    public boolean deleteRule(Rules rule) {
        long id = rule.getId();
        database.delete(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_RULES,
                com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.RULES_COLUMN_ID + " = " + id, null);
        return true;
    }

    public List<Rules> getAllRules() {
        List<Rules> items = new ArrayList<Rules>();
        Cursor cursor = database.query(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.TABLE_RULES,
                rulesAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rules rule = cursorToRule(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public Rules cursorToRule(Cursor cursor) {
        Rules rule = new Rules();
        rule.setId(cursor.getLong(0));//id
        rule.setTimeLimit(cursor.getLong(1));//time limit
        rule.setCardDisplayTime(cursor.getLong(2));//card display time
        rule.setMaxCardCount(cursor.getInt(3));//max card count
        rule.setCardTypes(cursor.getString(4));//card types
        rule.setDeckId(cursor.getLong(5));
        return rule;
    }

    public String[] getRulesAllColumns(){
        return rulesAllColumns;
    }
    /************************ RULES METHODS END *******************************/

    /************************ SETTINGS METHODS START *******************************/
    public Settings createSettings(int numberOfConnections, String userName) {
        ContentValues values = new ContentValues();
        values.put(QuizSQLiteHelper.SETTINGS_COLUMN_USERNAME, userName);
        values.put(QuizSQLiteHelper.SETTINGS_COLUMN_NUMBEROFCONNECTIONS, numberOfConnections);
        long insertId = database.insert(QuizSQLiteHelper.TABLE_SETTINGS,
                null, values);
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_SETTINGS,
                settingsAllColumns, QuizSQLiteHelper.SETTINGS_COLUMN_ID
                        + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Settings newSettings = cursorToSetting(cursor);
        cursor.close();
        return newSettings;
    }

    public Settings createSettings(Settings settings) {
        return createSettings(settings.getNumberOfConntections(), settings.getUserName());
    }

    public int updateSettings(Settings s){
        ContentValues cv = new ContentValues();
        cv.put(QuizSQLiteHelper.SETTINGS_COLUMN_ID, s.getId());
        cv.put(QuizSQLiteHelper.SETTINGS_COLUMN_USERNAME, s.getUserName());
        cv.put(QuizSQLiteHelper.SETTINGS_COLUMN_NUMBEROFCONNECTIONS, s.getNumberOfConntections());
        String where = QuizSQLiteHelper.SETTINGS_COLUMN_ID + " = " + s.getId();
        return database.update(QuizSQLiteHelper.TABLE_SETTINGS, cv, where, null);
    }

    public boolean deleteSetting(Settings settings) {
        long id = settings.getId();
        database.delete(QuizSQLiteHelper.TABLE_SETTINGS,
                QuizSQLiteHelper.SETTINGS_COLUMN_ID + " = " + id, null);
        return true;
    }

    public List<Settings> getAllSettings() {
        List<Settings> settings = new ArrayList<>();
        Cursor cursor = database.query(QuizSQLiteHelper.TABLE_SETTINGS,
                settingsAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Settings s = cursorToSetting(cursor);
            settings.add(s);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return settings;
    }

    public Settings cursorToSetting(Cursor cursor) {
        Settings settings = new Settings();
        settings.setId(cursor.getLong(0));//id
        settings.setUserName(cursor.getString(1));//userName
        settings.setNumberOfConntections(cursor.getInt(2));//numberOfConnections
        return settings;
    }

    public String[] getSettingsAllColumns(){
        return settingsAllColumns;
    }
    /************************ SETTINGS METHODS END *******************************/

    /************************ QUERY BUILDING HELPER METHODS START *******************************/

    private String buildCardTypeWhereClause(String tableName, List<Constants.CARD_TYPES> cardTypes) {
        if (cardTypes == null || cardTypes.size() == 0) {
            return null;
        }
        tableName = tableName == null ? "" : tableName + ".";
        StringBuilder whereClause = new StringBuilder();
        int i = 0;
        while (cardTypes.size() != i + 1) {
            whereClause.append(tableName)
                    .append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CARDTYPE)
                    .append("=\'")
                    .append(cardTypes.get(i).ordinal())
                    .append("\' OR ");
            i++;
        }
        whereClause.append(tableName)
                .append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_CARDTYPE)
                .append("=\'")
                .append(cardTypes.get(i).ordinal())
                .append("\'");
        return "(" + whereClause.toString() + ")";
    }

    private String buildModeratorNeededWhereClasue(String tableName, boolean moderatorNeeded) {
        tableName = tableName == null ? "" : tableName + ".";
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(tableName)
                .append(com.seniordesign.wolfpack.quizinator.Database.QuizSQLiteHelper.CARD_COLUMN_MODERATORNEEDED)
                .append("=\'")
                .append(String.valueOf(moderatorNeeded))
                .append("\'");
        return whereClause.toString();
    }
    /************************ QUERY BUILDING HELPER METHODS END *******************************/
}
