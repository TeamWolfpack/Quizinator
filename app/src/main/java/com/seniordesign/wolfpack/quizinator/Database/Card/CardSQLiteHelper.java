package com.seniordesign.wolfpack.quizinator.Database.Card;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seniordesign.wolfpack.quizinator.Constants;

/**
 * SQLite data access object for Card
 * @creation 10/4/2016
 */
public class CardSQLiteHelper extends SQLiteOpenHelper {

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
        setDefaultCardSet();
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

    private void setDefaultCardSet() {
        Card[] cards = new Card[10];
        cards[0] = new Card();
        cards[0].setQuestion("1+1 = ?");
        cards[0].setCorrectAnswer("2");
        String[] answerArea = {"1","2","3","4"};
        cards[0].setPossibleAnswers(answerArea);
        cards[0].setCardType(Constants.SHORT_MULTIPLE_CHOICE);
        cards[0].setPoints(1);
        cards[0].setModeratorNeeded("False");
        cards[1] = new Card();
        cards[1].setQuestion("1*2 = 0");
        cards[1].setCorrectAnswer("False");
        cards[1].setCardType(Constants.SHORT_TRUE_FALSE);
        String[] answerAreaTF = {"True", "False"};
        cards[1].setPossibleAnswers(answerAreaTF);
        cards[1].setPoints(1);
        cards[1].setModeratorNeeded("False");
        cards[2] = new Card();
        cards[2].setQuestion("4*5 = 20");
        cards[2].setCorrectAnswer("True");
        cards[2].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[2].setPossibleAnswers(answerAreaTF);
        cards[2].setPoints(1);
        cards[2].setModeratorNeeded("False");
        cards[3] = new Card();
        cards[3].setQuestion("20*10 = 100");
        cards[3].setCorrectAnswer("False");
        cards[3].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[3].setPossibleAnswers(answerAreaTF);
        cards[3].setPoints(1);
        cards[3].setModeratorNeeded("False");
        cards[4] = new Card();
        cards[4].setQuestion("10*91 = 901");
        cards[4].setCorrectAnswer("False");
        cards[4].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[4].setPossibleAnswers(answerAreaTF);
        cards[4].setPoints(1);
        cards[4].setModeratorNeeded("False");
        cards[5] = new Card();
        cards[5].setQuestion("100^2 = 10000");
        cards[5].setCorrectAnswer("True");
        cards[5].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[5].setPossibleAnswers(answerAreaTF);
        cards[5].setPoints(1);
        cards[5].setModeratorNeeded("False");
        cards[6] = new Card();
        cards[6].setQuestion("10*102 = 1002");
        cards[6].setCorrectAnswer("False");
        cards[6].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[6].setPossibleAnswers(answerAreaTF);
        cards[6].setPoints(1);
        cards[6].setModeratorNeeded("False");
        cards[7] = new Card();
        cards[7].setQuestion("8/2 = 4");
        cards[7].setCorrectAnswer("True");
        cards[7].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[7].setPossibleAnswers(answerAreaTF);
        cards[7].setPoints(1);
        cards[7].setModeratorNeeded("False");
        cards[8] = new Card();
        cards[8].setQuestion("120/4 = 30");
        cards[8].setCorrectAnswer("True");
        cards[8].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[8].setPossibleAnswers(answerAreaTF);
        cards[8].setPoints(1);
        cards[8].setModeratorNeeded("False");
        cards[9] = new Card();
        cards[9].setQuestion("6*7 = 41");
        cards[9].setCorrectAnswer("False");
        cards[9].setCardType(Constants.SHORT_TRUE_FALSE);
        cards[9].setPossibleAnswers(answerAreaTF);
        cards[9].setPoints(1);
        cards[9].setModeratorNeeded("False");

        cardDataSource.open();
        for (Card card : cards) {
            cardDataSource.createCard(card);
        }
        cardDataSource.close();
    }
}
