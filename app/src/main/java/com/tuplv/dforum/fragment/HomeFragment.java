package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.AddPostsActivity;
import com.tuplv.dforum.activity.ListForumActivity;
import com.tuplv.dforum.activity.LoginActivity;
import com.tuplv.dforum.activity.ViewPostsActivity;
import com.tuplv.dforum.adapter.PostsAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements View.OnClickListener, OnPostClickListener {

    TextView tvNameAndTotalPostForum;
    RelativeLayout rlShowListForum;
    RecyclerView rvQA, rvShareKnowledge;
    FloatingActionButton fabAddPost;
    ImageView imgShowMoreForum;
    PostsAdapter postsQAAdapter, postsShareKnowledgeAdapter;
    List<Post> postsQA, postsShareKnowledge;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        rvQA = view.findViewById(R.id.rvQA);
        rvShareKnowledge = view.findViewById(R.id.rvShareKnowledge);
        fabAddPost = view.findViewById(R.id.fabAddPost);
        imgShowMoreForum = view.findViewById(R.id.imgShowMoreForum);
        tvNameAndTotalPostForum = view.findViewById(R.id.tvNameAndTotalPostForum);
        rlShowListForum = view.findViewById(R.id.rlShowListForum);

        fabAddPost.setOnClickListener(this);
        rlShowListForum.setOnClickListener(this);
        imgShowMoreForum.setOnClickListener(this);
    }

    private void loadDataToView() {
        postsQA = new ArrayList<>();
        postsQAAdapter = new PostsAdapter(getActivity(), R.layout.item_posts, postsQA, this);
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
                    if (Objects.requireNonNull(post).getStatus().equals(STATUS_ENABLE)){
                        if (post.getCategoryName().equalsIgnoreCase(HOI_DAP)) {
                            postsQA.add(post);
                        }
                        if (post.getCategoryName().equalsIgnoreCase(CHIA_SE_KIEN_THUC)) {
                            postsShareKnowledge.add(post);
                        }
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
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    startActivity(new Intent(getActivity(), AddPostsActivity.class));
                else {
                    Toast.makeText(getActivity(), "Bạn cần đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.rlShowListForum:
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataEvent(Forum forum) {
        String data = forum.getName();
        // Cập nhật dữ liệu ở đây
        Toast.makeText(getActivity(), "name forum: " + data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(getActivity(), ViewPostsActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }
}