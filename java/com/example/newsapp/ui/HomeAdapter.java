package com.example.newsapp.ui;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.NewsDetail;
import com.example.newsapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private ArrayList<News> mDataset;
    private Context context;
    private int frag;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView newsImage;
        public TextView newsTitle;
        public TextView newsTime;
        public ImageButton bookmark;
        public RelativeLayout main;
        public TextView newsCategory;
        public ViewHolder(View v) {
            super(v);
            newsImage = (ImageView) v.findViewById(R.id.news_image);
            newsTitle = (TextView) v.findViewById(R.id.news_title);
            newsTime = (TextView) v.findViewById(R.id.news_time);
            bookmark = (ImageButton) v.findViewById(R.id.imageButton);
            main = (RelativeLayout) v.findViewById(R.id.main);
            newsCategory = (TextView) v.findViewById(R.id.news_category);
        }
    }
    public HomeAdapter(ArrayList<News> myDataset, Context context, int frag) {
        this.context = context;
        mDataset = myDataset;
        this.frag = frag;
    }
    public HomeAdapter(){}
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder Holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final ViewHolder holder = Holder;

        final News news = mDataset.get(position);
        final String image = news.getImage();
        final String url = news.getUrl();
//        Picasso.get().load(image).resize(120, 140).into(holder.newsImage);
        Glide.with(context)
                .load(image)
                .into(holder.newsImage);
        final String title = news.getTitle();
        holder.newsTitle.setText(title);
        final String category = news.getCategory();
        holder.newsCategory.setText(category);
        final String time = news.getTime();
        holder.newsTime.setText(time);
        final String id = news.getId();
        final String publishedDate = news.getDate();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        final ArrayList<String> fetch = gson.fromJson(json, type);
        final ImageButton bookmarkIcon = holder.bookmark;
        if (fetch != null && fetch.contains(id)) {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_24);
        }
        else {
            holder.bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
        }


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
                    intent.putExtra("fromBookmark", ""+frag);
                    context.startActivity(intent);

                }
            });


        holder.main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                ImageView imageView = dialog.findViewById(R.id.image);
//                Picasso.get().load(news.getImage()).resize(120, 140).into(imageView);
                Glide.with(context)
                        .load(news.getImage())
                        .into(imageView);
                TextView textView = dialog.findViewById(R.id.text);
                textView.setText(news.getTitle());
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                Gson gson = new Gson();
                String json = mPrefs.getString("bookmarkIds", null);
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                final ArrayList<String> fetch = gson.fromJson(json, type);
                final ImageView bookmarkButton = dialog.findViewById(R.id.bookmarkLogo);
                if(fetch != null && fetch.contains(id)){
                    bookmarkButton.setImageResource(R.drawable.detail_bookmark_filled);
                }
                else{
                    bookmarkButton.setImageResource(R.drawable.detail_bookmark_empty);
                }
                ImageView imageView1 = dialog.findViewById(R.id.twitterShareLogo);
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tweetUrl = "https://twitter.com/intent/tweet?text=Check out this link:&url=" + news.getUrl()+"&hashtags=CSCI571NewsSearch";
                        Uri uri = Uri.parse(tweetUrl);
                        context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        dialog.dismiss();
                    }
                });
                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Gson gson = new Gson();
                        String json = mPrefs.getString("bookmarkIds", null);
                        Type type = new TypeToken<ArrayList<String>>() {}.getType();
                        final ArrayList<String> fetch = gson.fromJson(json, type);
                        final ImageView bookmarkButton = dialog.findViewById(R.id.bookmarkLogo);
                        if(fetch.contains(id)){
                            System.out.println("removing");
                            bookmarkButton.setImageResource(R.drawable.detail_bookmark_empty);
                            removeF(title,id,bookmarkIcon);
                            notifyDataSetChanged();
                        }
                        else{
                            System.out.println("adding");
                            bookmarkButton.setImageResource(R.drawable.detail_bookmark_filled);
                            addF(title,time,category,image,id,bookmarkIcon, url, publishedDate);
                            notifyDataSetChanged();
                        }
                        notifyDataSetChanged();
                    }
                });
                dialog.show();
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
                if (fetch != null && fetch.contains(id)) {
                    removeF(title, id, bookmarkIcon);

                } else {
                    addF(title, time, category, image, id, bookmarkIcon, url, publishedDate);
                }
            }
        });
        holder.itemView.setTag(position);

    }
    private void removeF(String title, String id, ImageButton bookmarkIcon) {
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
        System.out.println(bookmarkIcon);
        bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_border_24);
        notifyDataSetChanged();
        Toast.makeText(context, title+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
//        Tab2.update();
    }

    private void addF(String title, String time, String category, String image, String id, ImageButton bookmarkIcon, String url, String pub) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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
        bookmarkIcon.setImageResource(R.drawable.baseline_bookmark_24);
        Toast.makeText(context, title+" was added to bookmarks", Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
//        Tab2.update();
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }
}
