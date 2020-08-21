package com.example.newsapp.ui.headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.newsapp.R;
import com.google.android.material.tabs.TabLayout;

public class HeadlineFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_headline, container, false);
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        return root;
    }
}
