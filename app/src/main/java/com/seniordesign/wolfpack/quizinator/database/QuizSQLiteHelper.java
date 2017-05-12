package com.seniordesign.wolfpack.quizinator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class QuizSQLiteHelper extends SQLiteOpenHelper {

    // database filename
    private static final String DATABASE_NAME = "quizinator.db";
    private static final int DATABASE_VERSION = 3;

    // Card table contents
    static final String TABLE_CARDS = "cards";
    static final String CARD_COLUMN_ID = "_id";
    static final String CARD_COLUMN_CARDTYPE = "_cardType";
    static final String CARD_COLUMN_QUESTION = "_question";
    static final String CARD_COLUMN_POSSIBLEANSWERS = "_possibleAnswers";
    static final String CARD_COLUMN_CORRECTANSWER = "_correctAnswer";
    static final String CARD_COLUMN_POINTS = "_points";
    static final String CARD_COLUMN_MODERATORNEEDED = "_moderatorNeeded";
    static final String CARD_COLUMN_UUID = "_uuid";
    static final String CARD_COLUMN_LASTMODIFIED = "_lastModified";

    // Deck table contents
    static final String TABLE_DECKS = "decks";
    static final String DECK_COLUMN_ID = "_id";
    static final String DECK_COLUMN_DECKNAME = "_deckName";
    static final String DECK_COLUMN_CATEGORY = "_category";
    static final String DECK_COLUMN_SUBJECT = "_subject";
    static final String DECK_COLUMN_DUPLICATECARDS = "_duplicateCards";
    static final String DECK_COLUMN_OWNER = "_owner";
    static final String DECK_COLUMN_UUID = "_uuid";

    // CardDeckRelations table contents
    static final String TABLE_CDRELATIONS = "cdrelations";
    static final String CDRELATIONS_COLUMN_ID = "_id";
    static final String CDRELATIONS_COLUMN_FKCARD = "_fkCard";
    static final String CDRELATIONS_COLUMN_FKDECK = "_fkDeck";

    //HighScore table contents
    static final String TABLE_HIGHSCORES = "highscores";
    static final String HIGHSCORES_COLUMN_ID = "_id";
    static final String HIGHSCORES_COLUMN_DECKID = "_deckID";
    static final String HIGHSCORES_COLUMN_BESTTIME = "_bestTime";
    static final String HIGHSCORES_COLUMN_BESTSCORE = "_bestScore";

    // Rules table contents
    static final String TABLE_RULESETS = "rulesets";
    static final String RULES_COLUMN_ID = "_id";
    static final String RULES_COLUMN_TIMELIMIT = "_timeLimit";
    static final String RULES_COLUMN_CARDDISPLAYTIME = "_cardDisplayTime";
    static final String RULES_COLUMN_MAXCARDCOUNT = "_maxCardCount";
    static final String RULES_COLUMN_CARDTYPES = "_cardTypes";
    static final String RULES_COLUMN_DECK_ID = "_deckId";
    static final String RULES_COLUMN_RULESET_NAME = "_ruleSetName";
    static final String RULES_COLUMN_DOUBLE_EDGE_SWORD = "_doubleEdgeSword";
    static final String RULES_COLUMN_LAST_CARD_WAGER = "_lastCardWager";
    static final String RULES_COLUMN_MULTIPLE_WINNERS = "multipleWinners";

    // Settings table contents
    static final String TABLE_SETTINGS = "settings";
    static final String SETTINGS_COLUMN_ID = "_id";
    static final String SETTINGS_COLUMN_USERNAME = "_userName";
    static final String SETTINGS_COLUMN_NUMBEROFCONNECTIONS = "_numberOfConnections";

    // Cards table creation sql statement
    static final String CARDS_TABLE_CREATE = "create table if not exists "
            + TABLE_CARDS + "("
            + CARD_COLUMN_ID + " integer primary key autoincrement, "
            + CARD_COLUMN_CARDTYPE + " INTEGER, "
            + CARD_COLUMN_QUESTION + " TEXT, "
            + CARD_COLUMN_CORRECTANSWER + " TEXT, "
            + CARD_COLUMN_POSSIBLEANSWERS + " TEXT, "
            + CARD_COLUMN_POINTS + " INTEGER DEFAULT 1, "
            + CARD_COLUMN_MODERATORNEEDED + " TEXT, "
            + CARD_COLUMN_UUID + " TEXT UNIQUE, "
            + CARD_COLUMN_LASTMODIFIED + " INTEGER"
            + ");";

    // Decks table creation sql statement
    static final String DECKS_TABLE_CREATE = "create table if not exists "
            + TABLE_DECKS + "("
            + DECK_COLUMN_ID + " integer primary key autoincrement, "
            + DECK_COLUMN_DECKNAME + " TEXT, "
            + DECK_COLUMN_CATEGORY + " TEXT, "
            + DECK_COLUMN_SUBJECT + " TEXT, "
            + DECK_COLUMN_DUPLICATECARDS + " TEXT, "
            + DECK_COLUMN_OWNER + " TEXT, "
            + DECK_COLUMN_UUID + " TEXT UNIQUE"
            + ");";

    // CardDeckRelations table creation sql statement
    static final String CDRELATIONS_TABLE_CREATE = "create table if not exists "
            + TABLE_CDRELATIONS + "("
            + CDRELATIONS_COLUMN_ID + " integer primary key autoincrement, "
            + CDRELATIONS_COLUMN_FKCARD + " TEXT, "
            + CDRELATIONS_COLUMN_FKDECK + " INTEGER"
            + ");";

    // HighScores table creation sql statement
    static final String HIGHSCORES_TABLE_CREATE = "create table if not exists "
            + TABLE_HIGHSCORES + "("
            + HIGHSCORES_COLUMN_ID + " integer primary key autoincrement, "
            + HIGHSCORES_COLUMN_DECKID + " INTEGER, "
            + HIGHSCORES_COLUMN_BESTTIME + " REAL, "
            + HIGHSCORES_COLUMN_BESTSCORE + " INTEGER"
            + ");";

    // Rules table creation sql statement
    static final String RULES_TABLE_CREATE = "create table if not exists "
            + TABLE_RULESETS + "("
            + RULES_COLUMN_ID + " integer primary key autoincrement, "
            + RULES_COLUMN_TIMELIMIT + " REAL, "
            + RULES_COLUMN_CARDDISPLAYTIME + " REAL, "
            + RULES_COLUMN_MAXCARDCOUNT + " INTEGER, "
            + RULES_COLUMN_CARDTYPES + " TEXT, "
            + RULES_COLUMN_DECK_ID + " INTEGER, "
            + RULES_COLUMN_RULESET_NAME + " TEXT, "
            + RULES_COLUMN_DOUBLE_EDGE_SWORD + " TEXT DEFAULT NULL, "
            + RULES_COLUMN_LAST_CARD_WAGER + " TEXT DEFAULT NULL, "
            + RULES_COLUMN_MULTIPLE_WINNERS + " TEXT DEFAULT NULL"
            + ");";

    // Settings table creation sql statement, table columns
    static final String SETTINGS_TABLE_CREATE = "create table if not exists "
            + TABLE_SETTINGS + "("
            + SETTINGS_COLUMN_ID + " integer primary key autoincrement, "
            + SETTINGS_COLUMN_USERNAME + " TEXT, "
            + SETTINGS_COLUMN_NUMBEROFCONNECTIONS + " INTEGER"
            + ");";

    QuizSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARDS_TABLE_CREATE);
        db.execSQL(DECKS_TABLE_CREATE);
        db.execSQL(CDRELATIONS_TABLE_CREATE);
        db.execSQL(HIGHSCORES_TABLE_CREATE);
        db.execSQL(RULES_TABLE_CREATE);
        db.execSQL(SETTINGS_TABLE_CREATE);
        DBDefaultTableSetup.setupDefaultTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2)
            DBUpgrader.upgradeToV2(db);
        if(oldVersion < 3)
            DBUpgrader.upgradeToV3(db);
    }
}
