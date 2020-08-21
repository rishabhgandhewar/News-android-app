package com.example.newsapp.ui.headlines;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new TabWorld();
            case 1:
                return new TabBusiness();
            case 2:
                return new TabPolitics();
            case 3:
                return new TabSports();
            case 4:
                return new TabTechnology();
            case 5:
                return new TabScience();
        }
        return null; //does not happen
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "World";
            case 1:
                return "Business";
            case 2:
                return "Politics";
            case 3:
                return "Sports";
            case 4:
                return "Technology";
            case 5:
                return "Science";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 6; //three fragments
    }
}