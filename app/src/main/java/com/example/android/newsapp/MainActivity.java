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
//implement Loader Manager for AsyncTaskLoader and NewsAdapter.ItemClickListener to get ID from interface
//defined in NewsAdapter
public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Void>,NewsAdapter.ItemClickListener {
    static final String TAG = "MainActivity";
    //to update the view
    private ProgressBar progress;
    private RecyclerView rv;
    private NewsAdapter adapter;
    //database tools
    private Cursor cursor;
    private SQLiteDatabase db;
    //predefined value to help with AsyncTaskLoader
    private static final int NEWS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        rv = (RecyclerView) findViewById(R.id.displayNews);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //checks preferences to see if app has been opened before
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirst = prefs.getBoolean("isfirst", true);
        //if app has not been opened, set isFirst to false in preferences and force a refresh
        //so that database is filled automatically
        if (isFirst) {
            load();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isfirst", false);
            editor.commit();
        }
        //initiate firebase scheduler to refresh based on ScheduleUtilities' function scheduleRefresh
        ScheduleUtilities.scheduleRefresh(this);
    }
    //inflate refresh button into menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    //when refresh button in menu is pressed, force a refresh with load() function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();

        if (itemNumber == R.id.search) {
            load();
        }

        return true;
    }

    //when app is opened or gone back to
    @Override
    protected void onStart() {
        super.onStart();
        //get most recent reference to db
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        //load cursor with function from database utils
        cursor = DatabaseUtils.getAll(db);
        //set adapter for Recylerview
        adapter = new NewsAdapter(cursor, this);
        rv.setAdapter(adapter);
        //make visibility go away for bug (when the app is returned to from browser the progress spinner
        //still spins) so this kills it
        progress.setVisibility(GONE);
    }

    //on closing the app close the database and cursor
    @Override
    protected void onStop() {
        super.onStop();
        db.close();
        cursor.close();
    }

    //asynctaskloader defining functions
    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Void>(this) {

            //set progress spinner to visible on load start
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progress.setVisibility(View.VISIBLE);
            }

            //trigger refresh articles from refreshtasks in background
            @Override
            public Void loadInBackground() {
                RefreshTasks.refreshArticles(MainActivity.this);
                return null;
            }

        };
    }

    //when loader is finished
    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        //close the progress spinner
        progress.setVisibility(GONE);
        //create new reference to updated database and cursor
        db = new DBHelper(MainActivity.this).getReadableDatabase();
        cursor = DatabaseUtils.getAll(db);
        //reset adapter for recyclerview to get newest information loaded from database
        adapter = new NewsAdapter(cursor, this);
        rv.setAdapter(adapter);
        //let the adapter know the data set changed by calling function
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {

    }

    //fulfills interface from Recyclerview adapter
    @Override
    public void onItemClick(Cursor cursor, int clickedItemIndex) {
        //when recyclerview node is clicked this finds the index and gets the url for that
        //particular node's newsitem from the database
        cursor.moveToPosition(clickedItemIndex);
        String url = cursor.getString(cursor.getColumnIndex(Contract.TABLE_ARTICLES.COLUMN_NAME_URL));
        Log.d(TAG, String.format("Url %s", url));
        //start intent to open browser with new url
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    //triggers the start of the loader
    public void load() {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER, null, this).forceLoad();

    }

}
