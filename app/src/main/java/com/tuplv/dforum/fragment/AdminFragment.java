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

import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.AddForumActivity;

public class AdminFragment extends Fragment implements View.OnClickListener {

    Button btnPostsApprove, btnAddForum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        init(view);

        btnPostsApprove.setOnClickListener(this);
        btnAddForum.setOnClickListener(this);

        return view;
    }

    private void init(View view) {
        btnPostsApprove = view.findViewById(R.id.btnPostsApprove);
        btnAddForum = view.findViewById(R.id.btnAddForum);
    }

    @SuppressLint("NonConstantResourceId, CommitTransaction")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPostsApprove:
                Toast.makeText(view.getContext(), "Kiểm duyệt", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAddForum:
                startActivity(new Intent(getActivity(), AddForumActivity.class));
                break;
        }
    }
}