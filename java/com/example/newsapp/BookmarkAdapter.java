package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.ui.News;
import com.example.newsapp.ui.bookmark.BookmarkFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView newsImage;
        public TextView newsTitle;
        public TextView newsDate;
        public ImageButton bookmark;
        public LinearLayout main;
        public TextView newsCategory;
        public ViewHolder(View v) {
            super(v);
            newsImage = (ImageView) v.findViewById(R.id.image);
            newsTitle = (TextView) v.findViewById(R.id.title);
            newsDate = (TextView) v.findViewById(R.id.date);
            bookmark = (ImageButton) v.findViewById(R.id.imageButton);
            main = (LinearLayout) v.findViewById(R.id.main);
            newsCategory = (TextView) v.findViewById(R.id.category);
        }
    }

    public BookmarkAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bookmark_grid, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, int position) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        final ArrayList<String> fetch = gson.fromJson(json, type);
        Gson gsonTmp = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        if (fetch != null) {
            String positionNo = fetch.get(position);
            String elementJson = mPrefs.getString(positionNo, null);
            final News news = gsonTmp.fromJson(elementJson, News.class);
            if(news != null) {
                final String image = news.getImage();
                final String url = news.getUrl();
//                Picasso.get().load(image).resize(150, 150).into(holder.newsImage);
                Glide.with(context)
                        .load(image)
                        .into(holder.newsImage);
                final String title = news.getTitle();
                holder.newsTitle.setText(title);
                final String category = news.getCategory();
                holder.newsCategory.setText(category);
                final String time = news.getDate();
                String month[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                StringBuilder sb_date = new StringBuilder();

                String year = time.substring(0,4);
                String day = time.substring(8,10);
                int month_index = Integer.parseInt(time.substring(5,7))-1;
                String mon = month[month_index];

                sb_date.append(day);
                sb_date.append(" ");
                sb_date.append(mon);
                sb_date.append(" ");
                holder.newsDate.setText(sb_date.toString());
                final String id = news.getId();
                holder.bookmark.setImageResource(R.drawable.fav_bookmark_filled);


                holder.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String isBookmarked = "";
                        if (fetch != null && fetch.contains(title)) {
                            isBookmarked = "true";
                        } else {
                            isBookmarked = "false";
                        }
                        System.out.println("Starting detail activity");
                        Intent intent = new Intent(context, NewsDetail.class);
                        intent.putExtra("id", news.getId());
                        intent.putExtra("isBookmarked", isBookmarked);
                        intent.putExtra("title", news.getTitle());
                        intent.putExtra("time", news.getTime());
                        intent.putExtra("category", news.getCategory());
                        intent.putExtra("image", news.getImage());
                        intent.putExtra("url", news.getUrl());
                        intent.putExtra("pub_date", news.getDate());
                        intent.putExtra("fromBookmark", "Yes");
                        context.startActivity(intent);

                    }
                });


                holder.main.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog);
                        ImageView imageView = dialog.findViewById(R.id.image);
//                        Picasso.get().load(news.getImage()).resize(120, 140).into(imageView);
                        Glide.with(context)
                                .load(news.getImage())
                                .into(imageView);
                        TextView textView = dialog.findViewById(R.id.text);
                        textView.setText(news.getTitle());
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Gson gson = new Gson();
                        String json = mPrefs.getString("bookmarkIds", null);
                        Type type = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        final ArrayList<String> fetch = gson.fromJson(json, type);
                        final ImageView bookmarkButton = dialog.findViewById(R.id.bookmarkLogo);
                        bookmarkButton.setImageResource(R.drawable.detail_bookmark_filled);
                        bookmarkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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
                                bookmarkButton.setImageResource(R.drawable.detail_bookmark_empty);
                                Toast.makeText(context, title+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                BookmarkFragment.update();
                            }
                        });
                        dialog.show();
                        ImageView imageView1 = dialog.findViewById(R.id.twitterShareLogo);
                        imageView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tweetUrl = "https://twitter.com/intent/tweet?text=Check out this link:&url=" + news.getUrl() + "&hashtags=CSCI571NewsSearch";
                                Uri uri = Uri.parse(tweetUrl);
                                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                dialog.dismiss();
                            }
                        });
                        dialog.setCanceledOnTouchOutside(true);
                        return false;
                    }
                });
                holder.bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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
                        Toast.makeText(context, title+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
                        BookmarkFragment.update();
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        return fetch == null ? 0 : fetch.size();
    }
}
