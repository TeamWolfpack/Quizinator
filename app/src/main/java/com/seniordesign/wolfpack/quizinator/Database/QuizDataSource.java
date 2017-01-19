package com.seniordesign.wolfpack.quizinator.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.seniordesign.wolfpack.quizinator.Database.Card.CardSQLiteHelper;
import com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation.CdrSQLiteHelper;
import com.seniordesign.wolfpack.quizinator.Database.Deck.DeckSQLiteHelper;

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

}
