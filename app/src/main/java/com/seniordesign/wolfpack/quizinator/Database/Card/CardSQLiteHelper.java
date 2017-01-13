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

    //database filename
    private static final String DATABASE_NAME = "card.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CARDS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CARDTYPE + " INTEGER, "
            + COLUMN_QUESTION + " TEXT, "
            + COLUMN_CORRECTANSWER + " TEXT, "
            + COLUMN_POSSIBLEANSWERS + " TEXT, "
            + COLUMN_POINTS + " INTEGER, "
            + COLUMN_MODERATORNEEDED + " TEXT"
            + ");";


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

        StringBuilder queryBuilder = new StringBuilder()
                .append("insert into " + TABLE_CARDS)
                .append(" SELECT \'" + "1" + "\' AS \'" + COLUMN_ID + "\',")
                    .append("\'").append(Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal()).append("\' AS \'").append(COLUMN_CARDTYPE).append("\',")
                    .append("\'").append("1+1 = ?").append("\' AS \'").append(COLUMN_QUESTION).append("\',")
                    .append("\'").append("2").append("\' AS \'").append(COLUMN_CORRECTANSWER).append("\',")
                    .append("\'").append(gson.toJson(new String[]{"1", "2", "3", "4"})).append("\' AS \'").append(COLUMN_POSSIBLEANSWERS).append("\',")
                    .append("\'").append("1").append("\' AS \'").append(COLUMN_POINTS).append("\',")
                    .append("\'").append(FALSE).append("\' AS \'").append(COLUMN_MODERATORNEEDED).append("\' ")
                .append("UNION ALL SELECT \'" + "2" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal())
                    .append("\', ").append("\'1*2 = 0\', ")
                    .append("\'").append(FALSE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "3" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'4*5 = 20\', ").append("\'").append(TRUE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ").append("\'1\', ")
                    .append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "4" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'20*10 = 100\', ").append("\'").append(FALSE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "5" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'10*91 = 901\', ").append("\'").append(FALSE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "6" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'100^2 = 10000\', ").append("\'").append(TRUE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "7" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'10*102 = 1002\', ").append("\'").append(FALSE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "8" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'8/2 = 4\', ").append("\'").append(TRUE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "9" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'120/4 = 30\', ").append("\'").append(TRUE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\' ")
                .append("UNION ALL SELECT \'" + "10" + "\', " + "\'")
                    .append(Constants.CARD_TYPES.TRUE_FALSE.ordinal()).append("\', ")
                    .append("\'6*7 = 41\', ").append("\'").append(FALSE).append("\', ")
                    .append("\'").append(gson.toJson(TRUE_FALSE_ANSWERS)).append("\', ")
                    .append("\'1\', ").append("\'").append(FALSE).append("\';");

//        String queryBuilder = ("insert into " + TABLE_CARDS) +
//                " SELECT \'" + "1" + "\' AS \'" + COLUMN_ID + "\'," +
//                "\'" + Constants.CARD_TYPES.MULTIPLE_CHOICE.ordinal() + "\' AS \'" + COLUMN_CARDTYPE + "\'," +
//                "\'" + "1+1 = ?" + "\' AS \'" + COLUMN_QUESTION + "\'," +
//                "\'" + "2" + "\' AS \'" + COLUMN_CORRECTANSWER + "\'," +
//                "\'" + gson.toJson(new String[]{"1", "2", "3", "4"}) + "\' AS \'" + COLUMN_POSSIBLEANSWERS + "\'," +
//                "\'" + "1" + "\' AS \'" + COLUMN_POINTS + "\'," +
//                "\'" + FALSE + "\' AS \'" + COLUMN_MODERATORNEEDED + "\' " +
//                "UNION ALL SELECT \'" + "2" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() +
//                "\', " + "\'1*2 = 0\', " +
//                "\'" + FALSE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "3" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'4*5 = 20\', " + "\'" + TRUE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " + "\'1\', " +
//                "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "4" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'20*10 = 100\', " + "\'" + FALSE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "5" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'10*91 = 901\', " + "\'" + FALSE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "6" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'100^2 = 10000\', " + "\'" + TRUE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "7" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'10*102 = 1002\', " + "\'" + FALSE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "8" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'8/2 = 4\', " + "\'" + TRUE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "9" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'120/4 = 30\', " + "\'" + TRUE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\' " +
//                "UNION ALL SELECT \'" + "10" + "\', " + "\'" +
//                Constants.CARD_TYPES.TRUE_FALSE.ordinal() + "\', " +
//                "\'6*7 = 41\', " + "\'" + FALSE + "\', " +
//                "\'" + gson.toJson(TRUE_FALSE_ANSWERS) + "\', " +
//                "\'1\', " + "\'" + FALSE + "\';";

        db.execSQL(queryBuilder.toString());
    }
}
