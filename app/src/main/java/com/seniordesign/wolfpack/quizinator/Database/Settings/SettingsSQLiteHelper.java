package com.seniordesign.wolfpack.quizinator.Database.Settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite data access object for Settings
 * @creation 10/4/2016
 */
public class SettingsSQLiteHelper extends SQLiteOpenHelper {

    //table contents
    public static final String TABLE_RULES = "settings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "_userName";
    public static final String COLUMN_NUMBEROFCONNECTIONS = "_numberOfConnections";

    //database filename
    private static final String DATABASE_NAME = "settings.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement, table columns
    private static final String DATABASE_CREATE = "create table "
            + TABLE_RULES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_USERNAME + " TEXT, "
            + COLUMN_NUMBEROFCONNECTIONS + " REAL, "
            + ");";

    /*
     * @author kuczynskij (10/4/2016)
     */
    public SettingsSQLiteHelper(Context context) {
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
        Log.w(SettingsSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion
                        + " to " + newVersion + ", which will " +
                        "destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
        onCreate(db);
    }
}
