package com.example.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.newsapp.ui.News;
import com.example.newsapp.ui.bookmark.BookmarkFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NewsDetail extends AppCompatActivity {

    private String url = "http://csci571-newsandroid.us-east-1.elasticbeanstalk.com/detailGuardian?id=";
    private String guardianURL = "";
    ActionBar actionBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("In detail activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_custom_view_detail);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final String id = intent.getStringExtra("id");
        final String isBookmarked = intent.getStringExtra("isBookmarked");
        final String newsTitle = intent.getStringExtra("title");
        final String newsTime = intent.getStringExtra("time");
        final String newsCategory = intent.getStringExtra("category");
        final String newsImage = intent.getStringExtra("image");
        final String newsUrl = intent.getStringExtra("url");
        final String publishedDate = intent.getStringExtra("pub_date");
        final String fromBookmark = intent.getStringExtra("fromBookmark");
        final View view = actionBar.getCustomView();
        TextView textView = view.findViewById(R.id.title);
        textView.setText(newsTitle);
        System.out.println(id);
        StringBuilder sb = new StringBuilder(url);
        sb.append(id);
        url = sb.toString();
        ImageView imageView = view.findViewById(R.id.twitterShareLogo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetUrl = "https://twitter.com/intent/tweet?text=Check out this link:&url=" + guardianURL+"&hashtags=CSCI571NewsSearch";
                Uri uri = Uri.parse(tweetUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        final ImageView imageView1 = view.findViewById(R.id.bookmarkLogo);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(NewsDetail.this);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        final ArrayList<String> fetch = gson.fromJson(json, type);
        if(fetch != null && fetch.contains(id)){
            imageView1.setImageResource(R.drawable.detail_bookmark_filled);
        }
        else{
            imageView1.setImageResource(R.drawable.detail_bookmark_empty);
        }
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(NewsDetail.this);
                Gson gson = new Gson();
                String json = mPrefs.getString("bookmarkIds", null);
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                final ArrayList<String> fetch = gson.fromJson(json, type);
                final ImageView bookmarkButton = view.findViewById(R.id.bookmarkLogo);
                if(fetch != null && fetch.contains(id)){
                    removeF(newsTitle,id,imageView1, fromBookmark);
                }
                else{
                    addF(newsTitle,newsTime,newsCategory,newsImage,id,imageView1, newsUrl, publishedDate, fromBookmark);
                }
            }
        });
        loadNews();

    }

    private void removeF(String title, String id, ImageView bookmarkIcon, String fromBookmark) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        fetch.remove(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        gson = new Gson();
        json = gson.toJson(fetch);
        editor.putString("bookmarkIds", json);
        editor.apply();

        editor.remove(id);
        editor.apply();
        bookmarkIcon.setImageResource(R.drawable.detail_bookmark_empty);
        Toast.makeText(this, title+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
        if(fromBookmark.equals("Yes"))
            BookmarkFragment.update();
    }

    private void addF(String title, String time, String category, String image, String id, ImageView bookmarkIcon, String url, String pub, String fromBookmark) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch == null) {
            fetch = new ArrayList<String>();
        }
        fetch.add(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        News news = new News(title, id, time, category, image, url, pub);
        gson = new Gson();
        json = gson.toJson(fetch);
        editor.putString("bookmarkIds", json);
        editor.apply();
        Gson gsonTmp = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String jsonTmp = gsonTmp.toJson(news);
        editor.putString(id, jsonTmp);
        editor.commit();
        bookmarkIcon.setImageResource(R.drawable.detail_bookmark_filled);
        Toast.makeText(this, title+" was added to bookmarks", Toast.LENGTH_SHORT).show();
        if(fromBookmark.equals("Yes"))
            BookmarkFragment.update();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void loadNews() {

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarHome);
        final TextView fetching = (TextView) findViewById(R.id.fetching);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        fetching.setVisibility(View.GONE);
                        try{
                            JSONObject obj = new JSONObject(response);
                            String title = obj.getString("title");
                            String image = obj.getString("image");
                            String pub_date = obj.getString("published_date");
                            String description = obj.getString("description");
                            url = obj.getString("url");
                            guardianURL = url;
                            String section = obj.getString("section");

                            System.out.println(section);

                            String month[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                            StringBuilder sb_date = new StringBuilder();

                            String year = pub_date.substring(0,4);
                            String day = pub_date.substring(8,10);
                            int month_index = Integer.parseInt(pub_date.substring(5,7))-1;
                            String mon = month[month_index];

                            sb_date.append(day);
                            sb_date.append(" ");
                            sb_date.append(mon);
                            sb_date.append(" ");
                            sb_date.append(year);

                            TextView titletext = findViewById(R.id.news_title);
                            titletext.setText(title);

                            TextView datetext = findViewById(R.id.date);
                            datetext.setText(sb_date.toString());

                            TextView desctext = findViewById(R.id.description);
                            desctext.setMovementMethod(LinkMovementMethod.getInstance());
                            desctext.setText(Html.fromHtml(description));
                            TextView sectiontext = findViewById(R.id.category);
                            sectiontext.setText(section);

                            ImageView imageView = findViewById(R.id.news_image);
//                            Picasso.get().load(image).resize(120, 140).into(imageView);
                            Glide.with(getApplicationContext())
                                    .load(image)
                                    .into(imageView);

                            TextView HyperLink = findViewById(R.id.fullArticleLink);
                            Spanned Text;

                            Text = Html.fromHtml("<a href='"+url+"'>View Full Article</a>");
                            HyperLink.setMovementMethod(LinkMovementMethod.getInstance());
                            HyperLink.setText(Text);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewsDetail.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(NewsDetail.this);
        requestQueue.add(stringRequest);
    }
}
