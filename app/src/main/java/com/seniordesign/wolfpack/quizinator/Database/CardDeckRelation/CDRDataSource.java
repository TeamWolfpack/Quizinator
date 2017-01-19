package com.seniordesign.wolfpack.quizinator.Database.CardDeckRelation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CdrDataSource {

    // Database fields
    private SQLiteDatabase database;
    private CdrSQLiteHelper dbHelper;
    private String[] allColumns = {
            CdrSQLiteHelper.COLUMN_ID,
            CdrSQLiteHelper.COLUMN_FKCARD,
            CdrSQLiteHelper.COLUMN_FKDECK
    };

//    public CdrDataSource(Context context) {
//        dbHelper = new CdrSQLiteHelper(context);
//    }
//
//    public boolean open() throws SQLException {
//        database = dbHelper.getWritableDatabase();
//        return database.isOpen();
//    }
//
//    public boolean close() {
//        dbHelper.close();
//        return true;
//    }
//
//    public SQLiteDatabase getDatabase(){
//        return database;
//    }
//
//    public CdrSQLiteHelper getSQLiteHelper() {
//        return dbHelper;
//    }

//    public CardDeckRelation createCardDeckRelation(long fkCard, long fkDeck) {
//        ContentValues values = new ContentValues();
//        values.put(CdrSQLiteHelper.COLUMN_FKCARD, fkCard);
//        values.put(CdrSQLiteHelper.COLUMN_FKDECK, fkDeck);
//
//        long insertId = database.insert(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                null, values);
//        Cursor cursor = database.query(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                allColumns, CdrSQLiteHelper.COLUMN_ID
//                        + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        CardDeckRelation newCardDeckRelation = cursorToCardDeckRelation(cursor);
//        cursor.close();
//        return newCardDeckRelation;
//    }
//
//    public CardDeckRelation createCardDeckRelation(CardDeckRelation cdRelation) {
//        ContentValues values = new ContentValues();
//        values.put(CdrSQLiteHelper.COLUMN_FKCARD, cdRelation.getFkCard());
//        values.put(CdrSQLiteHelper.COLUMN_FKDECK, cdRelation.getFkDeck());
//
//        long insertId = database.insert(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                null, values);
//        Cursor cursor = database.query(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                allColumns, CdrSQLiteHelper.COLUMN_ID
//                        + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        CardDeckRelation newCardDeckRelation = cursorToCardDeckRelation(cursor);
//        cursor.close();
//        return newCardDeckRelation;
//    }
//
//    /*
//     * @author  chuna (10/4/2016)
//     */
//    public int deleteCardDeckRelation(CardDeckRelation cdRelation) {
//        long id = cdRelation.getId();
//        System.out.println("Deleted card: " + cdRelation.toString());
//        return database.delete(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                CdrSQLiteHelper.COLUMN_ID + " = " + id, null);
//    }
//
//    /*
//     * @author  chuna (10/4/2016)
//     */
//    public List<CardDeckRelation> getAllCardDeckRelations() {
//        List<CardDeckRelation> cdRelations = new ArrayList<>();
//        Cursor cursor = database.query(CdrSQLiteHelper.TABLE_CDRELATIONS,
//                allColumns, null, null, null, null, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            CardDeckRelation cdRelation = cursorToCardDeckRelation(cursor);
//            cdRelations.add(cdRelation);
//            cursor.moveToNext();
//        }
//        // make sure to close the cursor
//        cursor.close();
//        return cdRelations;
//    }
//
//    public CardDeckRelation cursorToCardDeckRelation(Cursor cursor) {
//        CardDeckRelation cdRelation = new CardDeckRelation();
//        cdRelation.setId(cursor.getLong(0));
//        cdRelation.setFkCard(cursor.getLong(1));
//        cdRelation.setFkDeck(cursor.getLong(2));
//        return cdRelation;
//    }
//
//    public String[] getAllColumns(){
//        return allColumns;
//    }
}
