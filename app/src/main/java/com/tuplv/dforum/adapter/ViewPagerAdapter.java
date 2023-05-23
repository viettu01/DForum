package com.tuplv.dforum.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.tuplv.dforum.fragment.AccountFragment;
import com.tuplv.dforum.fragment.ForumFragment;
import com.tuplv.dforum.fragment.HomeFragment;
import com.tuplv.dforum.fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    Context context;

    public ViewPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new SearchFragment();
            case 2:
                return new ForumFragment();
            case 3:
                return new AccountFragment();
            case 0:
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
