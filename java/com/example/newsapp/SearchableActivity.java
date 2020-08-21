package com.example.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.ui.News;
import com.example.newsapp.ui.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<JSONArray> data = new ArrayList<>();
    private ArrayList<News> newsItem = new ArrayList<>();
    private ArrayList<String> token = new ArrayList<>();
    private boolean fromInternalActivity = false;
    private String url = "http://csci571-newsandroid.us-east-1.elasticbeanstalk.com/searchGuardian?query=";
    ActionBar actionBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("In search");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        handleIntent(getIntent());
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.rgb(56, 3, 153));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                newsItem = new ArrayList<>();
                loadNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
            String query = intent.getStringExtra("query");
            actionBar.setTitle("Search Results for " + query);
            StringBuilder sb = new StringBuilder(url);
            sb.append(query);
            url = sb.toString();
            recyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            loadNews();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit){
        return unit.between(d1, d2);
    }

    private void loadNews() {
        newsItem = new ArrayList<>();
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarHome);
        final TextView fetching = (TextView) findViewById(R.id.fetching);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        fetching.setVisibility(View.GONE);
                        try {
                            newsItem = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            int len = array.length();
                            if(array.length() == 0){
                                TextView no = (TextView) findViewById(R.id.no);
                                no.setVisibility(View.VISIBLE);
                            }
                            for(int i = 0; i < array.length(); i++){
                                JSONObject newsObject = array.getJSONObject(i);
                                LocalDateTime ldt = LocalDateTime.now();
                                System.out.println(ldt);//Local date time
                                ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
                                ZonedDateTime LA = ldt.atZone( zoneId );
                                Instant now = Instant.parse(newsObject.getString("published_date"));
                                ZonedDateTime newsTime = now.atZone( zoneId );
                                System.out.println(i+" news date "+newsTime);
                                System.out.println(i+" now time "+LA);
                                long diff = zonedDateTimeDifference(newsTime, LA, ChronoUnit.SECONDS);
                                String timeAgo = "";
                                if (diff < 60) {
                                    timeAgo = diff+"s ago";
                                } else if (diff < 60 * 60) {
                                    timeAgo = diff/60+"m ago";
                                } else if (diff < 60 * 60 * 24) {
                                    timeAgo = diff/3600+"h ago";
                                } else {
                                    timeAgo = diff/86400+"d ago";
                                }
                                News news = new News(newsObject.getString("title"), newsObject.getString("id"), timeAgo, newsObject.getString("section"),newsObject.getString("image"), newsObject.getString("url"), newsObject.getString("published_date"));
                                newsItem.add(news);
                            }
                            adapter = new SearchAdapter(newsItem, SearchableActivity.this);
                            if (0 == recyclerView.getItemDecorationCount()) {
                                recyclerView.addItemDecoration(new DividerItemDecoration(SearchableActivity.this, DividerItemDecoration.VERTICAL));
                            }
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchableActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        fromInternalActivity = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(fromInternalActivity) {
            newsItem = new ArrayList<>();
            loadNews();
        }

        fromInternalActivity = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
