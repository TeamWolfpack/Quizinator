package com.seniordesign.wolfpack.quizinator.Database.HighScore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for high scores
 * @creation 10/4/2016.
 */
public class HighScoresDataSource {

    // Database fields
    private SQLiteDatabase database;
    private HighScoresSQLiteHelper dbHelper;
    private String[] allColumns = {
        HighScoresSQLiteHelper.COLUMN_ID,
        HighScoresSQLiteHelper.COLUMN_DECKNAME,
        HighScoresSQLiteHelper.COLUMN_BESTTIME,
        HighScoresSQLiteHelper.COLUMN_BESTSCORE
    };

    /*
     * @author kuczynskij (10/4/2016)
     */
    public HighScoresDataSource(Context context) {
        dbHelper = new HighScoresSQLiteHelper(context);
    }

    /*
     * @author kuczynskij (10/10/2016)
     */
    public boolean open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return database.isOpen();
    }

    /*
     * @author kuczynskij (10/10/2016)
     */
    public boolean close() {
        dbHelper.close();
        return true;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public SQLiteDatabase getDatabase(){
        return database;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public HighScoresSQLiteHelper getSQLiteHelper() {
        return dbHelper;
    }

    /*
     * @author kuczynskij (10/10/2016)
     */
    public HighScores createHighScore(String deckName, long time, int bestScore) {
        ContentValues values = new ContentValues();
            values.put(HighScoresSQLiteHelper.COLUMN_DECKNAME, deckName);
            values.put(HighScoresSQLiteHelper.COLUMN_BESTTIME, time);
            values.put(HighScoresSQLiteHelper.COLUMN_BESTSCORE, bestScore);
        long insertId = database.insert(HighScoresSQLiteHelper.TABLE_HIGHSCORES,
                null, values);
        Cursor cursor = database.query(HighScoresSQLiteHelper.TABLE_HIGHSCORES,
                allColumns, HighScoresSQLiteHelper.COLUMN_ID
                        + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        HighScores newHighScores = cursorToHighScore(cursor);
        cursor.close();
        return newHighScores;
    }

    /*
     * @author kuczynskij (10/16/2016)
     */
    public void updateHighScore(long id, HighScores hs) {
        ContentValues cv = new ContentValues();
            cv.put(HighScoresSQLiteHelper.COLUMN_DECKNAME, hs.getDeckName());
            cv.put(HighScoresSQLiteHelper.COLUMN_BESTTIME, hs.getBestTime());
            cv.put(HighScoresSQLiteHelper.COLUMN_BESTSCORE, hs.getBestScore());
        String where = HighScoresSQLiteHelper.COLUMN_ID + " = " + id;
        database.update(HighScoresSQLiteHelper.TABLE_HIGHSCORES, cv, where, null);
    }

    /*
     * @author kuczynskij (10/10/2016)
     */
    public boolean deleteHighScore(HighScores scores) {
        long id = scores.getId();
        database.delete(HighScoresSQLiteHelper.TABLE_HIGHSCORES,
            HighScoresSQLiteHelper.COLUMN_ID + " = " + id, null);
        return true;
    }

    /*
     * @author kuczynskij (10/10/2016)
     */
    public List<HighScores> getAllHighScores() {
        List<HighScores> items = new ArrayList<HighScores>();
        Cursor cursor = database.query(HighScoresSQLiteHelper.TABLE_HIGHSCORES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HighScores rule = cursorToHighScore(cursor);
            items.add(rule);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    /*
     * @author kuczynskij (10/4/2016)
     */
    public HighScores cursorToHighScore(Cursor cursor) {
        HighScores scores = new HighScores();
            scores.setId(cursor.getLong(0));//id
            scores.setDeckName(cursor.getString(1));//deck name
            scores.setBestTime(cursor.getLong(2));//best time
            scores.setBestScore(cursor.getInt(3));//best score
        return scores;
    }

    public String[] getAllColumns(){
        return allColumns;
    }
}
