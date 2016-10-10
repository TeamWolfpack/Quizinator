package com.seniordesign.wolfpack.quizinator.Database.HighScore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite data access object for High Scores
 * @creation 10/4/2016
 */
public class HighScoresSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_HIGHSCORES = "highscores";
    //table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DECKNAME = "_deckName";
    public static final String COLUMN_BESTTIME = "_bestTime";
    public static final String COLUMN_BESTSCORE= "_bestScore";

    //database filename
    private static final String DATABASE_NAME = "highscores.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_HIGHSCORES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DECKNAME + " TEXT, "
            + COLUMN_BESTTIME + " REAL"
            + COLUMN_BESTSCORE + " INTEGER"
            + ");";

    /*
     * @author kuczynskij (10/4/2016)
     */
    public HighScoresSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(HighScoresSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORES);
        onCreate(db);
    }
}
