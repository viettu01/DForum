package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_POST;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.AddPostsActivity;
import com.tuplv.dforum.activity.ListForumActivity;
import com.tuplv.dforum.adapter.PostsAdapter;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    RecyclerView rvQA, rvShareKnowledge;
    FloatingActionButton fabAddPost;
    ImageView imgShowMoreForum;
    PostsAdapter postsQAAdapter, postsShareKnowledgeAdapter;
    List<Post> postsQA, postsShareKnowledge;


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

    private void loadDataToView() {

        postsQA = new ArrayList<>();
        postsQAAdapter = new PostsAdapter(getActivity(), R.layout.item_posts, postsQA);
        rvQA.setAdapter(postsQAAdapter);
        rvQA.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        postsShareKnowledge = new ArrayList<>();
        postsShareKnowledgeAdapter = new PostsAdapter(getActivity(), R.layout.item_posts, postsShareKnowledge);
        rvShareKnowledge.setAdapter(postsShareKnowledgeAdapter);
        rvShareKnowledge.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_POST).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getCategoryName().equalsIgnoreCase(HOI_DAP)) {
                        postsQA.add(post);
                    }
                    if (post.getCategoryName().equalsIgnoreCase(CHIA_SE_KIEN_THUC)) {
                        postsShareKnowledge.add(post);
                    }
                }
                postsQAAdapter.notifyDataSetChanged();
                postsShareKnowledgeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddPost:
                startActivity(new Intent(getActivity(), AddPostsActivity.class));
                break;
            case R.id.imgShowMoreForum:
                startActivity(new Intent(getActivity(), ListForumActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataToView();
    }
}