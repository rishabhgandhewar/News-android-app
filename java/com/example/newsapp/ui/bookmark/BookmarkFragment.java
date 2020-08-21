package com.example.newsapp.ui.bookmark;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.BookmarkAdapter;
import com.example.newsapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    static private RecyclerView mRecyclerView;
    static private RecyclerView.Adapter mAdapter;
    static private TextView no;
    static private SharedPreferences mPrefs;
    static private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View root = inflater.inflate(R.layout.fragment_bookmark, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.fragment_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new BookmarkAdapter(getContext());
        if (0 == mRecyclerView.getItemDecorationCount()) {
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }        mRecyclerView.setAdapter(mAdapter);
        no = (TextView) root.findViewById(R.id.no);
        Gson gson = new Gson();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);

        if (fetch == null || fetch.size() == 0) {
            no.setVisibility(View.VISIBLE);
        }
        else {
            no.setVisibility(View.INVISIBLE);
        }
        return root;
    }

    public static void update() {
        mAdapter = new BookmarkAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
//        no = (TextView) getView().findViewById(R.id.no);
//        Set<String> fetch = mPrefs.getStringSet("List", new HashSet<String>());
        Gson gson = new Gson();
        String json = mPrefs.getString("bookmarkIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch == null || fetch.size() == 0) {
            no.setVisibility(View.VISIBLE);
//            no.setText("get into here");
        }
        else {
            no.setVisibility(View.INVISIBLE);
        }
//        test.setText(Integer.toString(loves.size()));
    }
}
