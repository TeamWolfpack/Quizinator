package com.seniordesign.wolfpack.quizinator.Database.Deck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

/**
 * SQLite data access object for Deck
 * @creation 10/4/2016
 */
public class DeckSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_DECKS = "deck";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DECKNAME = "_deckName";
    public static final String COLUMN_CATEGORY = "_category";
    public static final String COLUMN_SUBJECT = "_subject";
    public static final String COLUMN_DUPLICATECARDS = "_duplicateCards";
    public static final String COLUMN_OWNER = "_owner";



    //database filename
    private static final String DATABASE_NAME = "deck.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DECKS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DECKNAME + " TEXT, "
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_SUBJECT + " TEXT, "
            + COLUMN_DUPLICATECARDS + " TEXT, "
            + COLUMN_OWNER + " TEXT "
            + ");";

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
        setDefaultDeckSet(db);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        onCreate(db);
    }

    private void setDefaultDeckSet(SQLiteDatabase db) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("insert into " + TABLE_DECKS)
                .append(" SELECT \'" + "1" + "\' AS \'" + COLUMN_ID + "\',")
                .append("\'").append("Default").append("\' AS \'").append(COLUMN_DECKNAME).append("\',")
                .append("\'").append("General").append("\' AS \'").append(COLUMN_CATEGORY).append("\',")
                .append("\'").append("General").append("\' AS \'").append(COLUMN_SUBJECT).append("\',")
                .append("\'").append(String.valueOf(true)).append("\' AS \'").append(COLUMN_DUPLICATECARDS).append("\',")
                .append("\'").append("Team Wolfpack").append("\' AS \'").append(COLUMN_OWNER).append("\';");

        db.execSQL(queryBuilder.toString());
    }
}
