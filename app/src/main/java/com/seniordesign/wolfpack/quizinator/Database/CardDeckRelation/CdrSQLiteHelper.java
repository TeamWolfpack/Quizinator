package com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seniordesign.wolfpack.quizinator.Database.Card.CardDataSource;
import com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation.CdrDataSource;

/**
 * Created by aaron on 1/10/2017.
 */

public class CdrSQLiteHelper extends SQLiteOpenHelper{

    //table contents
    public static final String TABLE_CDRELATIONS = "cdrelations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FKCARD = "_fkCard";
    public static final String COLUMN_FKDECK = "_fkDeck";

    private CardDataSource cardDataSource;

    //database filename
    private static final String DATABASE_NAME = "cdrelation.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CDRELATIONS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FKCARD + " INTEGER, "
            + COLUMN_FKDECK + " INTEGER"
            + ");";

    public CdrSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CdrSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CDRELATIONS);
        onCreate(db);
    }
}
