package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite data access object for Card
 * @creation 10/4/2016
 */
public class CardSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CARDTYPE = "_cardYype";
    public static final String COLUMN_QUESTION = "_question";
    public static final String COLUMN_POSSIBLEANSWERS = "_possibleAnswers";
    public static final String COLUMN_CORRECTANSWER = "_correctAnswer";
    public static final String COLUMN_POINTS = "_points";
    public static final String COLUMN_MODERATORNEEDED = "_moderatorNeeded";

    //database filename
    private static final String DATABASE_NAME = "card.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "";

    /*
     * @author  chuna (10/4/2016)
     */
    public CardSQLiteHelper(Context context) {
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
        Log.w(CardSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }
}
