package com.seniordesign.wolfpack.quizinator.database;

import android.database.sqlite.SQLiteDatabase;

import static com.seniordesign.wolfpack.quizinator.database.QuizSQLiteHelper.RULES_TABLE_CREATE;

class DBUpgrader {

    static void upgradeToV2(SQLiteDatabase db){
        ruleSetUpgradeV2(db);
    }

    private static void ruleSetUpgradeV2(SQLiteDatabase db){
        String TABLE_RULES = "rules";
        String DROP_RULES_TABLE = "DROP TABLE IF EXISTS " + TABLE_RULES;
        db.execSQL(DROP_RULES_TABLE);
        db.execSQL(RULES_TABLE_CREATE);
//        String RENAME_TABLE = "ALTER TABLE " + TABLE_RULES
//                + " RENAME TO " + QuizSQLiteHelper.TABLE_RULESETS + ";";
//        db.execSQL(RENAME_TABLE);
//        String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_RULESET_NAME + " TEXT;";
//        db.execSQL(ALTER_TABLE_ADD_COLUMN);
//        ALTER_TABLE_ADD_COLUMN = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_DOUBLE_EDGE_SWORD + " TEXT DEFAULT NULL;";
//        db.execSQL(ALTER_TABLE_ADD_COLUMN);
//        ALTER_TABLE_ADD_COLUMN = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_LAST_CARD_WAGER + " TEXT DEFAULT NULL;";
//        db.execSQL(ALTER_TABLE_ADD_COLUMN);
//        ALTER_TABLE_ADD_COLUMN = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_MULTIPLE_WINNERS + " TEXT DEFAULT NULL;";
//        db.execSQL(ALTER_TABLE_ADD_COLUMN);
    }
}
