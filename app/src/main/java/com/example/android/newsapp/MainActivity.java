package com.example.android.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.newsapp.models.Contract;
import com.example.android.newsapp.models.DBHelper;
import com.example.android.newsapp.models.DatabaseUtils;
import com.example.android.newsapp.models.NewsItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Void>,NewsAdapter.ItemClickListener {
    static final String TAG = "MainActivity";
    private ProgressBar progress;
    private RecyclerView rv;
    private NewsAdapter adapter;
    private Cursor cursor;
    private SQLiteDatabase db;

    private static final int NEWS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        rv = (RecyclerView) findViewById(R.id.displayNews);
        rv.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isfirst", true);
        if (isFirst) {
            load();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isfirst", false);
            editor.commit();
        }
        ScheduleUtilities.scheduleRefresh(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();

        if (itemNumber == R.id.search) {
            load();
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(db);
        adapter = new NewsAdapter(cursor, this);
        rv.setAdapter(adapter);
        progress.setVisibility(GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
        cursor.close();
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Void>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public Void loadInBackground() {
                RefreshTasks.refreshArticles(MainActivity.this);
                return null;
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        progress.setVisibility(GONE);
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(db);

        adapter = new NewsAdapter(cursor, this);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }

    @Override
    public void onItemClick(Cursor cursor, int clickedItemIndex) {
        cursor.moveToPosition(clickedItemIndex);
        String url = cursor.getString(cursor.getColumnIndex(Contract.TABLE_ARTICLES.COLUMN_NAME_URL));
        Log.d(TAG, String.format("Url %s", url));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void load() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER, null, this).forceLoad();

    }

    /*class NetworkTask extends AsyncTask<URL,Void, ArrayList<NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
            //query = search.getText().toString();

        }

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            ArrayList<NewsItem> result = null;
            URL url = NetworkUtilities.makeURL("the-next-web", "latest");
            Log.d(TAG, "url: " +url.toString());
            try {
                String json = NetworkUtilities.getResponseFromHttpUrl(url);
                result = NetworkUtilities.parseJSON(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final ArrayList<NewsItem> data) {
            super.onPostExecute(data);
            progress.setVisibility(View.GONE);
            if (data != null) {
                /*NewsAdapter adapter = new NewsAdapter(data, new NewsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int clickedItemIndex) {
                        String url = data.get(clickedItemIndex).getUrl();
                        Log.d(TAG, String.format("Url %s", url));
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });
                rv.setAdapter(adapter);


            }
        }

    }*/
}
