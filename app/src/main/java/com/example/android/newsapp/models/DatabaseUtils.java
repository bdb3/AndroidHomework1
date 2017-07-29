package com.example.android.newsapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static com.example.android.newsapp.models.Contract.TABLE_ARTICLES.*;


/**
 * Created by Danny on 7/27/2017.
 */

public class DatabaseUtils {

    //load all info from database into cursor sorted by date and return cursor
    public static Cursor getAll(SQLiteDatabase db) {
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME_DATE + " DESC"
        );
        return cursor;
    }
    //insert all values into database from Arraylist of newsitems taken from API
    public static void bulkInsert(SQLiteDatabase db, ArrayList<NewsItem> news) {

        db.beginTransaction();
        try {
            //for each newsitem use getters to get info to add to database
            for (NewsItem a : news) {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_NAME_TITLE, a.getTitle());
                cv.put(COLUMN_NAME_DESC, a.getDescription());
                cv.put(COLUMN_NAME_DATE, a.getDate());
                cv.put(COLUMN_NAME_IMGURL, a.getImgUrl());
                cv.put(COLUMN_NAME_URL, a.getUrl());
                db.insert(TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    //method for deleting table in database
    public static void deleteAll(SQLiteDatabase db) {
        db.delete(TABLE_NAME, null, null);
    }

}