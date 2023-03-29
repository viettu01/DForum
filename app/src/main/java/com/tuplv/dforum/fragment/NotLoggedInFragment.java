package com.tuplv.dforum.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.LoginActivity;
import com.tuplv.dforum.activity.RegisterActivity;

public class NotLoggedInFragment extends Fragment implements View.OnClickListener {

    Button btnCallLogin, btnCallRegister;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_not_logged_in, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        btnCallLogin = view.findViewById(R.id.btnCallLogin);
        btnCallLogin.setOnClickListener(this);

        btnCallRegister = view.findViewById(R.id.btnCallRegister);
        btnCallRegister.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCallLogin:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
            case R.id.btnCallRegister:
                startActivity(new Intent(getContext(), RegisterActivity.class));
                break;
        }
    }
}