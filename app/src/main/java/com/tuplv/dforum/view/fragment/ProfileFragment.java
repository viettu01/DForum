package com.tuplv.dforum.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tuplv.dforum.R;
import com.tuplv.dforum.viewmodel.AccountViewModel;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btnLogout;
    private AccountViewModel accountViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        accountViewModel = new AccountViewModel(getContext());

        return view;
    }

    private void init(View view){
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                accountViewModel.logout();
                break;
        }
    }
}