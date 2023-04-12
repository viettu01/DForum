package com.tuplv.dforum.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuplv.dforum.activity.main.MainActivity;
import com.tuplv.dforum.fragment.AdminFragment;
import com.tuplv.dforum.fragment.HomeFragment;
import com.tuplv.dforum.fragment.NotLoggedInFragment;
import com.tuplv.dforum.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                if (user != null)
                    return new ProfileFragment();
                else
                    return new NotLoggedInFragment();
            case 2:
                if (context instanceof MainActivity)
                    return new HomeFragment();
                else
                    return new AdminFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        if (context instanceof MainActivity) {
            return 2;
        }

        return 3;
    }
}
