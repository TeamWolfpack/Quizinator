package com.seniordesign.wolfpack.quizinator.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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

    private void cdRelationsUpgradeV3(SQLiteDatabase db){
        String FKCARDTEMP_COLUMN = "_fkCardTemp TEXT";
        String ADD_FKCARDTEMP_COLUMN = "ALTER TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_CDRELATIONS
                + " ADD " + FKCARDTEMP_COLUMN;
        db.execSQL(ADD_FKCARDTEMP_COLUMN);

        //TODO update rows
        updateFKCardValues(db);

        String DROP_FKCARDTEMP_COLUMN = "ALTER TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_CDRELATIONS
                + " DROP " + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD;
        db.execSQL(DROP_FKCARDTEMP_COLUMN);

        String RENAME_FKCARDTEMP_COLUMN = "ALTER TABLE IF EXISTS " + QuizSQLiteHelper.TABLE_CDRELATIONS
                + " RENAME TO " + QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD;
        db.execSQL(RENAME_FKCARDTEMP_COLUMN);
    }

    private void updateFKCardValues(SQLiteDatabase db){
        String[] cdRelationsAllColumns = {
                QuizSQLiteHelper.CDRELATIONS_COLUMN_ID,
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKCARD,
                QuizSQLiteHelper.CDRELATIONS_COLUMN_FKDECK};
        List<CardDeckRelation> cdRelations = new ArrayList<>();
        Cursor cursor = db.query(QuizSQLiteHelper.TABLE_CDRELATIONS,
                cdRelationsAllColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CardDeckRelation cdRelation = cursorToCardDeckRelation(cursor);
            cdRelations.add(cdRelation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
    }
}
