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
        db.execSQL(QuizSQLiteHelper.RULES_TABLE_CREATE);
        DBDefaultTableSetup.setDefaultRuleSet(db);
    }

    private static void highscoresUpgradeV2(SQLiteDatabase db){
        String DROP_HIGHSCORES_TABLE = "DROP TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_HIGHSCORES;
        db.execSQL(DROP_HIGHSCORES_TABLE);
        db.execSQL(QuizSQLiteHelper.HIGHSCORES_TABLE_CREATE);
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
            if (card.getId() <= 25)
                uuid = DBDefaultTableSetup.CARDS_UUID[(int)card.getId() - 1];
            db.execSQL("UPDATE " + QuizSQLiteHelper.TABLE_CARDS +
                    " SET " + QuizSQLiteHelper.CARD_COLUMN_UUID + " = \'" + uuid + "\'" +
                    " WHERE " + QuizSQLiteHelper.CARD_COLUMN_ID + " = " + card.getId() + ";");
        }
    }

    private void cdRelationsUpgradeV3(SQLiteDatabase db){
        String FKCARDTEMP_COLUMN = "_fkCardTemp";
        String ADD_FKCARDTEMP_COLUMN = "ALTER TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_CDRELATIONS
                + " ADD " + FKCARDTEMP_COLUMN + " TEXT";
        db.execSQL(ADD_FKCARDTEMP_COLUMN);

        String tempTableName = "tempCDRelations";
        String RENAME_TABLER = "ALTER TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_CDRELATIONS
                + " RENAME TO " + tempTableName;
        db.execSQL(RENAME_TABLER);

        Cursor cursor = db.query(tempTableName,
                new String[]{
                        QuizSQLiteHelper.CDRELATIONS_COLUMN_ID,
                        QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD},
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cursor uuidCursor = db.query(QuizSQLiteHelper.TABLE_CARDS,
                    new String[]{QuizSQLiteHelper.CARD_COLUMN_UUID},
                    QuizSQLiteHelper.CARD_COLUMN_ID + " = " + cursor.getLong(1),
                    null, null, null, null);
            uuidCursor.moveToFirst();
            while (!uuidCursor.isAfterLast()) {
                db.execSQL("UPDATE " + tempTableName +
                        " SET " + FKCARDTEMP_COLUMN + " = " + uuidCursor.getString(0) +
                        " WHERE " + QuizSQLiteHelper.CARD_COLUMN_ID + " = " + cursor.getLong(0));
            }
            uuidCursor.close();
            cursor.moveToNext();
        }
        cursor.close();

        db.execSQL(QuizSQLiteHelper.CDRELATIONS_TABLE_CREATE);

        String COPY_TABLE_OVER = "INSERT INTO " + QuizSQLiteHelper.TABLE_CDRELATIONS + "(" +
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD + ", " +
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK + ") " +
                "SELECT (" + FKCARDTEMP_COLUMN + ", " + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK +
                ") FROM " + tempTableName;
        db.execSQL(COPY_TABLE_OVER);

        db.execSQL("DROP TABLE IF EXISTS " + tempTableName);

    }
}
