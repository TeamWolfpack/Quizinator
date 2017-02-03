package com.seniordesign.wolfpack.quizinator.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

public class QuizSQLiteHelper extends SQLiteOpenHelper {

    // database filename
    public static final String DATABASE_NAME = "quizinator.db";
    private static final int DATABASE_VERSION = 1;

    // Card table contents
    public static final String TABLE_CARDS = "cards";
    public static final String CARD_COLUMN_ID = "_id";
    public static final String CARD_COLUMN_CARDTYPE = "_cardType";
    public static final String CARD_COLUMN_QUESTION = "_question";
    public static final String CARD_COLUMN_POSSIBLEANSWERS = "_possibleAnswers";
    public static final String CARD_COLUMN_CORRECTANSWER = "_correctAnswer";
    public static final String CARD_COLUMN_POINTS = "_points";
    public static final String CARD_COLUMN_MODERATORNEEDED = "_moderatorNeeded";

    // Deck table contents
    public static final String TABLE_DECKS = "decks";
    public static final String DECK_COLUMN_ID = "_id";
    public static final String DECK_COLUMN_DECKNAME = "_deckName";
    public static final String DECK_COLUMN_CATEGORY = "_category";
    public static final String DECK_COLUMN_SUBJECT = "_subject";
    public static final String DECK_COLUMN_DUPLICATECARDS = "_duplicateCards";
    public static final String DECK_COLUMN_OWNER = "_owner";

    // CardDeckRelations table contents
    public static final String TABLE_CDRELATIONS = "cdrelations";
    public static final String CDRELATIONS_COLUMN_ID = "_id";
    public static final String CDRELATIONS_COLUMN_FKCARD = "_fkCard";
    public static final String CDRELATIONS_COLUMN_FKDECK = "_fkDeck";

    // Cards table creation sql statement
    private static final String CARDS_TABLE_CREATE = "create table "
            + TABLE_CARDS + "("
            + CARD_COLUMN_ID + " integer primary key autoincrement, "
            + CARD_COLUMN_CARDTYPE + " INTEGER, "
            + CARD_COLUMN_QUESTION + " TEXT, "
            + CARD_COLUMN_CORRECTANSWER + " TEXT, "
            + CARD_COLUMN_POSSIBLEANSWERS + " TEXT, "
            + CARD_COLUMN_POINTS + " INTEGER DEFAULT 1, "
            + CARD_COLUMN_MODERATORNEEDED + " TEXT"
            + ");";

    // Decks table creation sql statement
    private static final String DECKS_TABLE_CREATE = "create table "
            + TABLE_DECKS + "("
            + DECK_COLUMN_ID + " integer primary key autoincrement, "
            + DECK_COLUMN_DECKNAME + " TEXT, "
            + DECK_COLUMN_CATEGORY + " TEXT, "
            + DECK_COLUMN_SUBJECT + " TEXT, "
            + DECK_COLUMN_DUPLICATECARDS + " TEXT, "
            + DECK_COLUMN_OWNER + " TEXT "
            + ");";

    // CardDeckRelations table creation sql statement
    private static final String CDRELATIONS_TABLE_CREATE = "create table "
            + TABLE_CDRELATIONS + "("
            + CDRELATIONS_COLUMN_ID + " integer primary key autoincrement, "
            + CDRELATIONS_COLUMN_FKCARD + " INTEGER, "
            + CDRELATIONS_COLUMN_FKDECK + " INTEGER"
            + ");";


    public QuizSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARDS_TABLE_CREATE);
        db.execSQL(DECKS_TABLE_CREATE);
        db.execSQL(CDRELATIONS_TABLE_CREATE);
        setDefaultCardSet(db);
        setDefaultDeckSet(db);
        setDefaultCdRelationSet(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(QuizSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CDRELATIONS);
        onCreate(db);
    }

    private void setDefaultCardSet(SQLiteDatabase db) {
        Gson gson = new Gson();

        final String[] TRUE_FALSE_ANSWERS = new String[]{"True", "False"};

        StringBuilder queryBuilder = new StringBuilder()
                .append("insert into " + TABLE_CARDS)
                .append(" SELECT \'" + "1" + "\' AS \'" + CARD_COLUMN_ID + "\',")
                    .append("\'").append(Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal()).append("\' AS \'").append(CARD_COLUMN_CARDTYPE).append("\',")
                    .append("\'").append("1+1 = ?").append("\' AS \'").append(CARD_COLUMN_QUESTION).append("\',")
                    .append("\'").append("2").append("\' AS \'").append(CARD_COLUMN_CORRECTANSWER).append("\',")
                    .append("\'").append(gson.toJson(new String[]{"2", "1", "4", "3"})).append("\' AS \'").append(CARD_COLUMN_POSSIBLEANSWERS).append("\',")
                    .append("\'").append("1").append("\' AS \'").append(CARD_COLUMN_POINTS).append("\',")
                    .append("\'").append(String.valueOf(false)).append("\' AS \'").append(CARD_COLUMN_MODERATORNEEDED).append("\' ")
                .append("UNION ALL SELECT \'" + "2" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal())
                    .append("\', ").append("\'1*2 = 0\', ")
                    .append("\'").append(String.valueOf(false)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "3" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'4*5 = 20\', ").append("\'").append(String.valueOf(true)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ").append("\'1\', ")
                    .append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "4" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'20*10 = 100\', ").append("\'").append(String.valueOf(false)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "5" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'10*91 = 901\', ").append("\'").append(String.valueOf(false)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "6" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'100^2 = 10000\', ").append("\'").append(String.valueOf(true)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "7" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'10*102 = 1002\', ").append("\'").append(String.valueOf(false)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "8" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'8/2 = 4\', ").append("\'").append(String.valueOf(true)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "9" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'120/4 = 30\', ").append("\'").append(String.valueOf(true)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\' ")
                .append("UNION ALL SELECT \'" + "10" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'6*7 = 41\', ").append("\'").append(String.valueOf(false)).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(String.valueOf(false)).append("\';");
        db.execSQL(queryBuilder.toString());
    }

    private void setDefaultDeckSet(SQLiteDatabase db) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("insert into " + TABLE_DECKS)
                .append(" SELECT \'" + "1" + "\' AS \'" + DECK_COLUMN_ID + "\',")
                .append("\'").append("Default").append("\' AS \'").append(DECK_COLUMN_DECKNAME).append("\',")
                .append("\'").append("General").append("\' AS \'").append(DECK_COLUMN_CATEGORY).append("\',")
                .append("\'").append("General").append("\' AS \'").append(DECK_COLUMN_SUBJECT).append("\',")
                .append("\'").append(String.valueOf(true)).append("\' AS \'").append(DECK_COLUMN_DUPLICATECARDS).append("\',")
                .append("\'").append("Team Wolfpack").append("\' AS \'").append(DECK_COLUMN_OWNER).append("\';");
        db.execSQL(queryBuilder.toString());
    }

    private void setDefaultCdRelationSet(SQLiteDatabase db) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("insert into " + TABLE_CDRELATIONS)
                .append(" SELECT \'1\' AS \'" + CDRELATIONS_COLUMN_ID + "\', ")
                    .append("\'1\' AS \'" + CDRELATIONS_COLUMN_FKCARD + "\', ")
                    .append("\'1\' AS \'" + CDRELATIONS_COLUMN_FKDECK + "\' ")
                .append(" UNION ALL SELECT \'2\', ")
                    .append("\'2\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'3\', ")
                    .append("\'3\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'4\', ")
                    .append("\'4\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'5\', ")
                    .append("\'5\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'6\', ")
                    .append("\'6\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'7\', ")
                    .append("\'7\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'8\', ")
                    .append("\'8\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'9\', ")
                    .append("\'9\', ")
                    .append("\'1\' ")
                .append(" UNION ALL SELECT \'10\', ")
                    .append("\'10\', ")
                    .append("\'1\';");
        db.execSQL(queryBuilder.toString());
    }
}
