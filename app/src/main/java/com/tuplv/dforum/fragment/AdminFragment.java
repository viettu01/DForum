package com.tuplv.dforum.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.tuplv.dforum.activity.post.PostApproveActivity;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.forum.ListForumActivity;

public class AdminFragment extends Fragment implements View.OnClickListener {

    Button btnPostsApprove, btnShowListForum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        init(view);

        btnPostsApprove.setOnClickListener(this);
        btnShowListForum.setOnClickListener(this);

        return view;
    }

    private void init(View view) {
        btnPostsApprove = view.findViewById(R.id.btnPostsApprove);
        btnShowListForum = view.findViewById(R.id.btnShowListForum);
    }

    @SuppressLint("NonConstantResourceId, CommitTransaction")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPostsApprove:
                startActivity(new Intent(getActivity(), PostApproveActivity.class));
                break;
            case R.id.btnShowListForum:
                startActivity(new Intent(getActivity(), ListForumActivity.class));
                break;
        }
    }
}