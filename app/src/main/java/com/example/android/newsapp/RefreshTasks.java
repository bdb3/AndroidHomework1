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


    public static void refreshArticles(Context context) {
        ArrayList<NewsItem> result = null;
        URL url = NetworkUtilities.makeURL("the-next-web", "latest");

        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

        try {
            DatabaseUtils.deleteAll(db);
            String json = NetworkUtilities.getResponseFromHttpUrl(url);
            result = NetworkUtilities.parseJSON(json);
            DatabaseUtils.bulkInsert(db, result);

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
    }
}