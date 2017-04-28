package com.seniordesign.wolfpack.quizinator.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.seniordesign.wolfpack.quizinator.database.QuizSQLiteHelper.HIGHSCORES_TABLE_CREATE;
import static com.seniordesign.wolfpack.quizinator.database.QuizSQLiteHelper.RULES_TABLE_CREATE;

class DBUpgrader {

    static void upgradeToV2(SQLiteDatabase db){
        ruleSetUpgradeV2(db);
        highscoresUpgradeV2(db);
    }

    private static void ruleSetUpgradeV2(SQLiteDatabase db){
        String TABLE_RULES = "rules";
        String DROP_RULES_TABLE = "DROP TABLE IF EXISTS " + TABLE_RULES;
        db.execSQL(DROP_RULES_TABLE);
        db.execSQL(RULES_TABLE_CREATE);
        DBDefaultTableSetup.setDefaultRuleSet(db);
    }

    private static void highscoresUpgradeV2(SQLiteDatabase db){
        String TABLE_HIGHSCORES = "highscores";
        String DROP_HIGHSCORES_TABLE = "DROP TABLE IF EXISTS " + TABLE_HIGHSCORES;
        db.execSQL(DROP_HIGHSCORES_TABLE);
        db.execSQL(HIGHSCORES_TABLE_CREATE);
        DBDefaultTableSetup.setDefaultRuleSet(db);
    }

    static void upgradeToV3(SQLiteDatabase db){
        // does the methods
    }

    private static void deckUuidUpgradeV3(SQLiteDatabase db) {
        List<Deck> decks = new ArrayList<>();
        Cursor cursor = db.query(QuizSQLiteHelper.TABLE_DECKS,
                new String[]{QuizSQLiteHelper.DECK_COLUMN_ID}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Deck deck = new Deck();
            deck.setId(cursor.getLong(0));
            decks.add(deck);
            cursor.moveToNext();
        }
        cursor.close();

        db.execSQL("ALTER TABLE " + QuizSQLiteHelper.TABLE_DECKS + " ADD COLUMN " +
                QuizSQLiteHelper.DECK_COLUMN_UUID + " TEXT UNIQUE");

        for (int i = 0; i < decks.size(); i++) {
            Deck deck = decks.get(i);
            String uuid = UUID.randomUUID().toString();
            if (deck.getId() == 1)
                uuid = DBDefaultTableSetup.DECK_UUID;
            db.execSQL("UPDATE " + QuizSQLiteHelper.TABLE_DECKS +
                    " SET " + QuizSQLiteHelper.DECK_COLUMN_UUID + " = \'" + uuid + "\'" +
                    " WHERE " + QuizSQLiteHelper.DECK_COLUMN_ID + " = " + deck.getId() + ";");
        }
    }

    private static void cardUuidUpgradeV3(SQLiteDatabase db) {
        List<Card> cards = new ArrayList<>();
        Cursor cursor = db.query(QuizSQLiteHelper.TABLE_CARDS,
                new String[]{QuizSQLiteHelper.CARD_COLUMN_ID}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = new Card();
            card.setId(cursor.getLong(0));
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();

        db.execSQL("ALTER TABLE " + QuizSQLiteHelper.TABLE_CARDS + " ADD COLUMN " +
                QuizSQLiteHelper.CARD_COLUMN_UUID + " TEXT UNIQUE");

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            String uuid = UUID.randomUUID().toString();
            if (card.getId() < 25)
                uuid = DBDefaultTableSetup.CARDS_UUID[(int) card.getId()];
            db.execSQL("UPDATE " + QuizSQLiteHelper.TABLE_CARDS +
                    " SET " + QuizSQLiteHelper.CARD_COLUMN_UUID + " = \'" + uuid + "\'" +
                    " WHERE " + QuizSQLiteHelper.CARD_COLUMN_ID + " = " + card.getId() + ";");
        }
    }
}
