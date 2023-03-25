package com.tuplv.dforum.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tuplv.dforum.R;
import com.tuplv.dforum.view.activity.AddPostsActivity;

public class HomeFragment extends Fragment {

    RecyclerView rvQA, rvShareKnowledge;
    FloatingActionButton fabAddPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddPostsActivity.class));
            }
        });

        return view;
    }

    private void init(View view) {
        rvQA = view.findViewById(R.id.rvQA);
        rvShareKnowledge = view.findViewById(R.id.rvShareKnowledge);
        fabAddPost = view.findViewById(R.id.fabAddPost);
    }
}