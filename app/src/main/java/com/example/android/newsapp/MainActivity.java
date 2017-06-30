package com.example.android.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.android.newsapp.models.NewsItem;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    private ProgressBar progress;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        rv = (RecyclerView) findViewById(R.id.displayNews);

        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NetworkTask task = new NetworkTask();
        task.execute();
        return true;
    }
    class NetworkTask extends AsyncTask<URL,Void, ArrayList<NewsItem>> {

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
                NewsAdapter adapter = new NewsAdapter(data, new NewsAdapter.ItemClickListener() {
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
    }
}
