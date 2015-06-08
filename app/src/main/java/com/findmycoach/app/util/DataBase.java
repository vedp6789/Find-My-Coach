package com.findmycoach.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by prem on 9/3/15.
 */
public class DataBase extends SQLiteOpenHelper {

    /**
     * Private variables
     */
    private static final String DB_NAME = "FindMyCoachDB";
    private static final String TABLE_NAME = "TableSubCategory";
    private static final int DB_VERSION = 2;
    private static final String CATEGORY = "category";
    private static final String _ID = "id";

    /**
     * Query to create table
     */
    private final static String query = "Create TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY + " TEXT);";

    /**
     * Drop table query used when DB version updates
     */
    private final static String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;

    /**
     * Static variable of type DataBase to make this class singleton
     */
    public static DataBase dataBase;

    private DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    /**
     * Static method for getting reference of DataBase
     */
    public static DataBase singleton(Context context) {
        if (dataBase == null)
            dataBase = new DataBase(context);
        return dataBase;
    }

    /**
     * Inserting sub categories into table TableSubCategory
     */
    public long insertData(String category) {
        Log.e("Database", category);
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY, category);
        db.insert(TABLE_NAME, null, contentValues);
        return 0;
    }

    /**
     * Select sub category with id
     */
    public String getAll() {
        Cursor c = db.query(TABLE_NAME, new String[]{CATEGORY}, null, null, null, null, null);
        c.moveToFirst();
        return c.getString(0);
    }

    /**
     * Clear all from database
     */
    public void clearDatabase() {
        Log.e("DataBase", "Delete");
        db.delete(TABLE_NAME, null, null);
    }


    /**
     * Create table if not present
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    /**
     * Deleting and then creating table in case any update is done to DB
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTable);
        onCreate(db);
    }
}
