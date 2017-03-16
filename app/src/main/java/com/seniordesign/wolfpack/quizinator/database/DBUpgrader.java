package com.seniordesign.wolfpack.quizinator.database;

import android.database.sqlite.SQLiteDatabase;

class DBUpgrader {

    static void upgradeToV2(SQLiteDatabase db){
//        String TABLE_RULES = "rules";
//        String RENAME_TABLE = "ALTER TABLE " + TABLE_RULES
//                + " RENAME TO " + QuizSQLiteHelper.TABLE_RULESETS + ";";
//        db.execSQL(RENAME_TABLE);
//        String ALTER_TABLE_ADD_COLUMNS = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_RULESET_NAME + " TEXT AND "
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_DOUBLE_EDGE_SWORD + " TEXT DEFAULT NULL AND "
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_LAST_CARD_WAGER + " TEXT DEFAULT NULL AND "
//                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_FASTEST_ANSWER + " TEXT DEFAULT NULL;";
//        db.execSQL(ALTER_TABLE_ADD_COLUMNS);
        ruleSetUpgradeV2(db);
    }

    private static void ruleSetUpgradeV2(SQLiteDatabase db){
        String TABLE_RULES = "rules";
        String RENAME_TABLE = "ALTER TABLE " + TABLE_RULES
                + " RENAME TO " + QuizSQLiteHelper.TABLE_RULESETS + ";";
        db.execSQL(RENAME_TABLE);
        String ALTER_TABLE_ADD_COLUMNS = "ALTER TABLE " + QuizSQLiteHelper.TABLE_RULESETS
                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_RULESET_NAME + " TEXT AND "
                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_DOUBLE_EDGE_SWORD + " TEXT DEFAULT NULL AND "
                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_LAST_CARD_WAGER + " TEXT DEFAULT NULL AND "
                + " ADD COLUMN " + QuizSQLiteHelper.RULES_COLUMN_FASTEST_ANSWER + " TEXT DEFAULT NULL;";
        db.execSQL(ALTER_TABLE_ADD_COLUMNS);
    }
}
