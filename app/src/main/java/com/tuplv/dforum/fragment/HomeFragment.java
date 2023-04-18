package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.OBJ_FORUM;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.forum.ListForumActivity;
import com.tuplv.dforum.activity.post.ListPostActivity;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, OnForumClickListener {

    RelativeLayout rlShowListForum;
    RecyclerView rvListForumFeatured;
    ImageView imvShowMoreForum;
    ForumAdapter forumAdapter;
    List<Forum> forums;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        rlShowListForum = view.findViewById(R.id.rlShowListForum);
        rvListForumFeatured = view.findViewById(R.id.rvListForumFeatured);
        imvShowMoreForum = view.findViewById(R.id.imvShowMoreForum);

        rlShowListForum.setOnClickListener(this);
        imvShowMoreForum.setOnClickListener(this);

        forums = new ArrayList<>();
    }

    private void getListForumFeatured() {
        forumAdapter = new ForumAdapter(requireActivity(), R.layout.item_forum, forums, this);
        rvListForumFeatured.setAdapter(forumAdapter);
        rvListForumFeatured.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).limitToLast(5)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        forums.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Forum forum = dataSnapshot.getValue(Forum.class);
                            forums.add(forum);
                        }
                        Collections.reverse(forums);
                        forumAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlShowListForum:
            case R.id.imvShowMoreForum:
                startActivity(new Intent(getActivity(), ListForumActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getListForumFeatured();
    }

    @Override
    public void goToActivityUpdate(Forum forum) {

    }

    @Override
    public void onDeleteClick(Forum forum) {

    }

    @Override
    public void goToListPostOfForum(Forum forum) {
        Intent intent = new Intent(getActivity(), ListPostActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }
}