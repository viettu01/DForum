package com.tuplv.dforum.fragment;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.LoginActivity;
import com.tuplv.dforum.model.Accounts;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Button btnLogout;
    TextView tvNickName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            getProfile("Accounts", user.getUid());
        return view;
    }

    private void init(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        tvNickName = view.findViewById(R.id.tvNickName);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
        requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
        requireActivity().finish();
    }

    public void getProfile(String DB, String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DB).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Accounts accounts = snapshot.getValue(Accounts.class);
                            if (accounts != null) {
                                tvNickName.setText(accounts.getNickName());
                            }
                        } else {
                            Toast.makeText(getContext(), "Tải dữ liệu lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Tải dữ liệu lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
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