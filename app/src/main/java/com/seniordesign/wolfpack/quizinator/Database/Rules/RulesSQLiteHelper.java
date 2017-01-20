package com.seniordesign.wolfpack.quizinator.Database.Rules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite data access object for Rules
 * @creation 10/4/2016
 */
public class RulesSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_RULES = "rules";
    //table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMELIMIT = "_timeLimit";
    public static final String COLUMN_CARDDISPLAYTIME = "_cardDisplayTime";
    public static final String COLUMN_MAXCARDCOUNT = "_maxCardCount";
    public static final String COLUMN_CARDTYPES = "_cardTypes";
    public static final String COLUMN_DECK_ID = "_deckId";

    //database filename
    private static final String DATABASE_NAME = "rules.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_RULES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TIMELIMIT + " REAL, "
            + COLUMN_CARDDISPLAYTIME + " REAL, "
            + COLUMN_MAXCARDCOUNT + " INTEGER, "
            + COLUMN_CARDTYPES + " TEXT, "
            + COLUMN_DECK_ID + " INTEGER"
            + ");";

    /*
     * @author chuna (10/4/2016)
     * @author kuczynskij (10/5/2016)
     */
    public RulesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * @author chuna (10/4/2016)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /*
     * @author chuna (10/4/2016)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RulesSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
        onCreate(db);
    }
}
