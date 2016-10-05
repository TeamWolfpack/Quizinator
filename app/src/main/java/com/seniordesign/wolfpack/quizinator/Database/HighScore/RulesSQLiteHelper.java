package com.seniordesign.wolfpack.quizinator.Database.HighScore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite data access object for Deck
 * @creation 10/4/2016
 */
public class RulesSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_RULES = "rules";

    //database filename
    private static final String DATABASE_NAME = "rules.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "";

    /*
     * @author  chuna (10/4/2016)
     */
    public RulesSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * @author  chuna (10/4/2016)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /*
     * @author  chuna (10/4/2016)
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
