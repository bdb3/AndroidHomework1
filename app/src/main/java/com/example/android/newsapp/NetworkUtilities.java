package com.example.android.newsapp;

import android.net.Uri;
import android.support.annotation.StringDef;
import android.util.Log;

import com.example.android.newsapp.models.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Danny on 6/21/2017.
 */

public class NetworkUtilities {
    public static final String TAG = "NetworkUtilities";
    public static final String NEWSAPI_BASE_URL = "https://newsapi.org/v1/articles";
    public static final String PARAM_SOURCE = "source";
    public static final String PARAM_SORTBY = "sortBy";
    public static final String PARAM_APIKEY = "apiKey";
    //TODO:replace APIKEY with working key
    public static final String APIKEY = "cacb8d48e33f438dbdcddabb90031126";

    public static URL makeURL(String source, String sortBy){
        Uri uri = Uri.parse(NEWSAPI_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_SOURCE, source)
                .appendQueryParameter(PARAM_SORTBY, sortBy)
                .appendQueryParameter(PARAM_APIKEY,APIKEY).build();
        URL url =null;
        try{
            String urlString = uri.toString();
            Log.d(TAG, "Url:" + urlString);
            url = new URL(uri.toString());

        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
    public static ArrayList<NewsItem> parseJSON(String json) throws JSONException {
        ArrayList<NewsItem> result = new ArrayList<>();
        JSONObject main = new JSONObject(json);
        JSONArray articles = main.getJSONArray("articles");

        for(int i = 0; i < articles.length(); i++){
            JSONObject item = articles.getJSONObject(i);
            String title = item.getString("title");
            String description = item.getString("description");
            String url = item.getString("url");
            String publishedAt = item.getString("publishedAt");
            NewsItem repo = new NewsItem(title, description, url, publishedAt);
            result.add(repo);
        }
        return result;
    }
}
