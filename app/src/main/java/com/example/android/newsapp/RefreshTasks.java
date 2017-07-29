package com.example.android.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.newsapp.models.DBHelper;
import com.example.android.newsapp.models.DatabaseUtils;
import com.example.android.newsapp.models.NewsItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Danny on 7/27/2017.
 */

public class RefreshTasks {

    public static final String ACTION_REFRESH = "refresh";

    //refresh database with new info
    public static void refreshArticles(Context context) {
        ArrayList<NewsItem> result = null;
        //create url
        URL url = NetworkUtilities.makeURL("the-next-web", "latest");
        //get reference to database
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

        try {
            //empty database
            DatabaseUtils.deleteAll(db);
            //get most recent response from api
            String json = NetworkUtilities.getResponseFromHttpUrl(url);
            //parse json result into arraylist
            result = NetworkUtilities.parseJSON(json);
            //insert arraylist into database
            DatabaseUtils.bulkInsert(db, result);

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //close database
        db.close();
    }
}