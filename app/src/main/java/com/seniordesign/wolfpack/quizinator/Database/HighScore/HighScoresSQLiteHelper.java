package com.seniordesign.wolfpack.quizinator.Database.HighScore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
<<<<<<< HEAD:app/src/main/java/com/seniordesign/wolfpack/quizinator/Database/HighScore/RulesSQLiteHelper.java
 * SQLite data access object for Deck
=======
 * SQLite data access object for Settings
>>>>>>> 9006be0154d6ca90869c9561d6695e65fb289d4e:app/src/main/java/com/seniordesign/wolfpack/quizinator/Database/HighScore/HighScoresSQLiteHelper.java
 * @creation 10/4/2016
 */
public class HighScoresSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_RULES = "highscores";

    //database filename
    private static final String DATABASE_NAME = "highscores.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "";

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
        onCreate(db);
    }
}
