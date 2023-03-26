package com.tuplv.dforum.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.AddPostsActivity;
import com.tuplv.dforum.activity.ListForumActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    RecyclerView rvQA, rvShareKnowledge;
    FloatingActionButton fabAddPost;
    ImageView imgShowMoreForum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        fabAddPost.setOnClickListener(this);
        imgShowMoreForum.setOnClickListener(this);

        return view;
    }

    private void init(View view) {
        rvQA = view.findViewById(R.id.rvQA);
        rvShareKnowledge = view.findViewById(R.id.rvShareKnowledge);
        fabAddPost = view.findViewById(R.id.fabAddPost);
        imgShowMoreForum = view.findViewById(R.id.imgShowMoreForum);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAddPost:
                startActivity(new Intent(getActivity(), AddPostsActivity.class));
                break;
            case R.id.imgShowMoreForum:
                startActivity(new Intent(getActivity(), ListForumActivity.class));
                break;
        }
    }
}