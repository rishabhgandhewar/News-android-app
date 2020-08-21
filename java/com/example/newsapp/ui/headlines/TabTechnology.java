package com.example.newsapp.ui.headlines;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import com.example.newsapp.R;
import com.example.newsapp.ui.HomeAdapter;
import com.example.newsapp.ui.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class TabTechnology extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<JSONArray> data = new ArrayList<>();
    private ArrayList<News> newsItem = new ArrayList<>();
    private ArrayList<String> token = new ArrayList<>();
    private Boolean fromInternalActivity = false;
    private final String url = "http://csci571-newsandroid.us-east-1.elasticbeanstalk.com/guardianTechnology";
    String city;
    String state;
    String description;
    View root;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.tab_technology, container, false);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.rgb(56, 3, 153));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod()
                mSwipeRefreshLayout.setRefreshing(true);
                newsItem = new ArrayList<>();
                loadNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView = (RecyclerView) root.findViewById(R.id.home_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        loadNews();

        return root;
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

            mSwipeRefreshLayout.setRefreshing(true);
            newsItem = new ArrayList<>();
            loadNews();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        fromInternalActivity = false;
    }

    private void loadNews() {
        newsItem = new ArrayList<>();
        final ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.progressBarHome);
        final TextView fetching = (TextView) root.findViewById(R.id.fetching);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        fetching.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(response);
                            int len = array.length();
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
                            adapter = new HomeAdapter(newsItem, getContext(), 5);
                            if (0 == recyclerView.getItemDecorationCount()) {
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    static long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit){
        return unit.between(d1, d2);
    }

}