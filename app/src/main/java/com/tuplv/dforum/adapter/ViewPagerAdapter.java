package com.tuplv.dforum.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuplv.dforum.fragment.AdminFragment;
import com.tuplv.dforum.fragment.HomeFragment;
import com.tuplv.dforum.fragment.NotLoggedInFragment;
import com.tuplv.dforum.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                if (user != null)
                    return new ProfileFragment();
                else
                    return new NotLoggedInFragment();
            case 2:
                return new AdminFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
