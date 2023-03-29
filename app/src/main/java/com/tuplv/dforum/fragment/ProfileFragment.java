package com.tuplv.dforum.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.LoginActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
        requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                logout();
                break;
        }
    }
}