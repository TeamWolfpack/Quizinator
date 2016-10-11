package com.seniordesign.wolfpack.quizinator.Database.Deck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
<<<<<<< HEAD:app/src/main/java/com/seniordesign/wolfpack/quizinator/Database/Deck/DeckSQLiteHelper.java
 * SQLite data access object for Deck
=======
 * SQLite data access object for Settings
>>>>>>> 9006be0154d6ca90869c9561d6695e65fb289d4e:app/src/main/java/com/seniordesign/wolfpack/quizinator/Database/Deck/RulesSQLiteHelper.java
 * @creation 10/4/2016
 */
public class DeckSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_DECK = "deck";
    public static final String COLUMN_ID = "_id";


    //database filename
    private static final String DATABASE_NAME = "deck.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "";

    /*
     * @author  chuna (10/4/2016)
     */
    public DeckSQLiteHelper(Context context) {
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
        Log.w(DeckSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECK);
        onCreate(db);
    }
}
