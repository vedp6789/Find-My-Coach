package com.findmycoach.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.findmycoach.app.beans.category.Category;
import com.findmycoach.app.beans.category.Datum;
import com.findmycoach.app.beans.category.DatumSub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prem on 9/3/15.
 */
public class DataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "FindMyCoachDB";
    private static final String TABLE_NAME = "TableSubCategory";
    private static final int DB_VERSION = 1;
    private static final String CATEGORY = "category";
    private static final String CATEGORY_ID = "category_id";
    private static final String SUBCATEGORY = "subcategory";
    private static final String SUBCATEGORY_ID = "subcategory_id";
    private static final String _ID = "id";


    private final static String query = "Create TABLE "+TABLE_NAME+" (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORY + " TEXT, " + SUBCATEGORY +" TEXT, " + CATEGORY_ID + " INTEGER, " + SUBCATEGORY_ID + " INTEGER);";
    private final static String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static SQLiteDatabase db;

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

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
        db.close();
    }

    public Category selectAllSubCategory(){
        //tableName, columns name in array, selection(where), selection args, groupby, having, orderby
        Cursor cursorCategory = db.query(TABLE_NAME, new String[]{CATEGORY, CATEGORY_ID}, null ,null, CATEGORY, null, null);
        List<Datum> datums = new ArrayList<Datum>();
        while (cursorCategory.moveToNext()){
            Datum datum = new Datum();
            datum.setName(cursorCategory.getString(0));
            datum.setId(cursorCategory.getString(1));
//            Log.d("FMC", datum.getName() + " : " + datum.getId());
//            Log.d("FMC", "*********************************************");

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
        Category category = new Category();
        category.setData(datums);
        category.setMessage("From db");
        db.close();
        return category;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTable);
        onCreate(db);
    }
}
