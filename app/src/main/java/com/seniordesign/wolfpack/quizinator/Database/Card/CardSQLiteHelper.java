package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.seniordesign.wolfpack.quizinator.Constants;

/**
 * SQLite data access object for Card
 * @creation 10/4/2016
 */
public class CardSQLiteHelper extends SQLiteOpenHelper {

//    private static Gson gson = new Gson(); //TODO initialize inside setDefaultCardSet() method later

    //table contents
    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CARDTYPE = "_cardType";
    public static final String COLUMN_QUESTION = "_question";
    public static final String COLUMN_POSSIBLEANSWERS = "_possibleAnswers";
    public static final String COLUMN_CORRECTANSWER = "_correctAnswer";
    public static final String COLUMN_POINTS = "_points";
    public static final String COLUMN_MODERATORNEEDED = "_moderatorNeeded";

    private CardDataSource cardDataSource;

    //database filename
    private static final String DATABASE_NAME = "card.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CARDS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CARDTYPE + " TEXT, "
            + COLUMN_QUESTION + " TEXT, "
            + COLUMN_CORRECTANSWER + " TEXT, "
            + COLUMN_POSSIBLEANSWERS + " TEXT, "
            + COLUMN_POINTS + " INTEGER, "
            + COLUMN_MODERATORNEEDED + " TEXT"
            + ");";

    /*
     * Constants specific to the initial cards
     * TODO change all of this into local variables inside the setDefaultCardSet() method
     */
//    private static final String TRUE = "True";
//    private static final String FALSE = "False";
//    private static final String[] TRUE_FALSE_ANSWERS = new String[]{"True", "False"};
//
//
//    private static final String INITIALIZE_DATABASE = "insert into " + TABLE_CARDS
//            + " SELECT \'" + Constants.SHORT_MULTIPLE_CHOICE + "\' AS \'" + COLUMN_CARDTYPE + "\',"
//                    + "\'" + "1+1 = ?" + "\' AS \'" + COLUMN_QUESTION + "\',"
//                    + "\'" + "2" + "\' AS \'" + COLUMN_CORRECTANSWER + "\',"
//                    + "\'" + gson.toJson(new String[]{"1","2","3","4"}) + "\' AS \'" + COLUMN_POSSIBLEANSWERS + "\',"
//                    + "\'" + "1" + "\' AS \'" + COLUMN_POINTS + "\',"
//                    + "\'" + FALSE + "\' AS \'" + COLUMN_MODERATORNEEDED + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'1*2 = 0\', "
//                    + "\'" + FALSE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'4*5 = 20\', "
//                    + "\'" + TRUE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'20*10 = 100\', "
//                    + "\'" + FALSE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'10*91 = 901\', "
//                    + "\'" + FALSE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'100^2 = 10000\', "
//                    + "\'" + TRUE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'10*102 = 1002\', "
//                    + "\'" + FALSE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'8/2 = 4\', "
//                    + "\'" + TRUE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'120/4 = 30\', "
//                    + "\'" + TRUE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//            + "UNION ALL SELECT \'" + Constants.SHORT_TRUE_FALSE + "\', "
//                    + "\'6*7 = 41\', "
//                    + "\'" + FALSE + "\', "
//                    + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
//                    + "\'1\', "
//                    + "\'" + FALSE + "\' "
//
//            + ");";

    /************************************************************************************************/

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
        setDefaultCardSet(db);
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

    private void setDefaultCardSet(SQLiteDatabase db) {
        Gson gson = new Gson();
        final String TRUE = "True";
        final String FALSE = "False";
        final String[] TRUE_FALSE_ANSWERS = new String[]{"True", "False"};

        final String INITIALIZE_DATABASE = "insert into " + TABLE_CARDS
                + " SELECT \'" + "1" + "\' AS \'" + COLUMN_ID + "\',"
                + "\'" + Constants.SHORT_MULTIPLE_CHOICE + "\' AS \'" + COLUMN_CARDTYPE + "\',"
                + "\'" + "1+1 = ?" + "\' AS \'" + COLUMN_QUESTION + "\',"
                + "\'" + "2" + "\' AS \'" + COLUMN_CORRECTANSWER + "\',"
                + "\'" + gson.toJson(new String[]{"1","2","3","4"}) + "\' AS \'" + COLUMN_POSSIBLEANSWERS + "\',"
                + "\'" + "1" + "\' AS \'" + COLUMN_POINTS + "\',"
                + "\'" + FALSE + "\' AS \'" + COLUMN_MODERATORNEEDED + "\' "
                + "UNION ALL SELECT \'" + "2" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'1*2 = 0\', "
                + "\'" + FALSE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "3" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'4*5 = 20\', "
                + "\'" + TRUE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "4" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'20*10 = 100\', "
                + "\'" + FALSE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "5" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'10*91 = 901\', "
                + "\'" + FALSE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "6" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'100^2 = 10000\', "
                + "\'" + TRUE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "7" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'10*102 = 1002\', "
                + "\'" + FALSE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "8" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'8/2 = 4\', "
                + "\'" + TRUE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "9" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'120/4 = 30\', "
                + "\'" + TRUE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\' "
                + "UNION ALL SELECT \'" + "10" + "\', "
                + "\'" + Constants.SHORT_TRUE_FALSE + "\', "
                + "\'6*7 = 41\', "
                + "\'" + FALSE + "\', "
                + "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', "
                + "\'1\', "
                + "\'" + FALSE + "\';";

        db.execSQL(INITIALIZE_DATABASE);
    }
}
