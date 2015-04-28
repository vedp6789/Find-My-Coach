package com.findmycoach.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prem on 9/3/15.
 */
public class DataBase extends SQLiteOpenHelper {

    /** Private variables */
    private static final String DB_NAME = "FindMyCoachDB";
    private static final String TABLE_NAME = "TableSubCategory";
    private static final int DB_VERSION = 1;
    private static final String CATEGORY = "category";
    private static final String CATEGORY_ID = "category_id";
    private static final String SUBCATEGORY = "subcategory";
    private static final String SUBCATEGORY_ID = "subcategory_id";

    private static final String _ID = "id";

    /** Query to create table */
    private final static String query = "Create TABLE "+TABLE_NAME+" (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY + " TEXT, " + SUBCATEGORY +" TEXT, " + CATEGORY_ID + " INTEGER, " + SUBCATEGORY_ID + " INTEGER);";

    /** Drop table query used when DB version updates */
    private final static String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase db;

    /** Static variable of type DataBase to make this class singleton */
    public static DataBase dataBase;

    private DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    /** Static method for getting reference of DataBase */
    public static DataBase singleton(Context context){
        if(dataBase == null)
            dataBase = new DataBase(context);
        return dataBase;
    }

    /** Inserting sub categories into table TableSubCategory */
    public void insertData(Category category){
        ContentValues contentValues = new ContentValues();
        for(Datum datum : category.getData()) {
            for(DatumSub datumSub : datum.getDataSub()){
                contentValues.put(CATEGORY, datum.getName());
                contentValues.put(SUBCATEGORY, datumSub.getName());
                contentValues.put(CATEGORY_ID, datum.getId());
                contentValues.put(SUBCATEGORY_ID, datumSub.getId());
                db.insert(TABLE_NAME,null,contentValues);
            }
        }
    }

    /** Select sub category with id */
    public String getSubCategory(String id){
        Cursor c = db.query(TABLE_NAME, new String[]{SUBCATEGORY}, SUBCATEGORY_ID + " = ?", new String[]{id}, null, null, null);
        c.moveToFirst();
        return c.getString(0);
    }

    /** Selecting all sub categories from database */
    public Category selectAllSubCategory(){
        /** Selecting all from table*/
        //tableName, columns name in array, selection(where), selection args, groupby, having, orderby
        Cursor cursorCategory = db.query(TABLE_NAME, new String[]{CATEGORY, CATEGORY_ID}, null ,null, CATEGORY, null, null);

        /** Creating list of Datum object */
        List<Datum> datums = new ArrayList<Datum>();
        while (cursorCategory.moveToNext()){
            Datum datum = new Datum();
            datum.setName(cursorCategory.getString(0));
            datum.setId(cursorCategory.getString(1));
//            Log.d("FMC", datum.getName() + " : " + datum.getId());
//            Log.d("FMC", "*********************************************");

            /** Creating list of DatumSub, them add it to respective Datum object */
            List<DatumSub> datumSubs = new ArrayList<DatumSub>();
            //tableName, columns name in array, selection(where), selection args, groupby, having, orderby
            Cursor cursorSubCategory = db.query(TABLE_NAME, new String[]{SUBCATEGORY, SUBCATEGORY_ID}, CATEGORY_ID + " = ?" ,new String[]{datum.getId()} , SUBCATEGORY, null, null);
            while(cursorSubCategory.moveToNext()){
                DatumSub datumSub = new DatumSub();
                datumSub.setId(cursorSubCategory.getString(1));
                datumSub.setName(cursorSubCategory.getString(0));
//                Log.d("FMC", datumSub.getName() + " : " + datumSub.getId());
                datumSubs.add(datumSub);
            }
//            Log.e("FMC", "*********************************************");
            datum.setDataSub(datumSubs);
            datums.add(datum);
        }

        /** Creating Category object using list of Datum, and custom message */
        Category category = new Category();
        category.setData(datums);
        category.setMessage("From db");
        return category;
    }

    public void clearDatabase(){
        db.delete(TABLE_NAME, null, null);
        Log.e("FMC","DELTETED");
    }


    /** Create table if not present */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    /** Deleting and then creating table in case any update is done to DB*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTable);
        onCreate(db);
    }
}
