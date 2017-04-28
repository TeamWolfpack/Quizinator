package com.seniordesign.wolfpack.quizinator.database;

import android.database.sqlite.SQLiteDatabase;

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

    
}
