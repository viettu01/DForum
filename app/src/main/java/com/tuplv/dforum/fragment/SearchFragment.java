package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment implements OnPostClickListener {

    EditText edtSearch;
    TextView tvNoSearch;
    RecyclerView rvSearchPost;
    List<Post> allPosts, searchPosts;
    PostAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        init(view);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isBlank()) {
                    tvNoSearch.setVisibility(View.VISIBLE);
                    tvNoSearch.setText("Nhập tiêu đề hoặc nội dung \n vào thanh tìm kiếm để tìm kiếm bài viết");
                    rvSearchPost.setVisibility(View.GONE);
                    return;
                }
                tvNoSearch.setVisibility(View.GONE);
                rvSearchPost.setVisibility(View.VISIBLE);
                searchPosts.clear();
                searchPost(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void init(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        tvNoSearch = view.findViewById(R.id.tvNoSearch);
        rvSearchPost = view.findViewById(R.id.rvSearchPost);

        allPosts = new ArrayList<>();
        searchPosts = new ArrayList<>();

        getAllPost();
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allPosts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            allPosts.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchPost(String search) {
        postAdapter = new PostAdapter(getContext(), R.layout.item_post, searchPosts, this);
        rvSearchPost.setAdapter(postAdapter);
        rvSearchPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        if (!search.isBlank()) {
            for (Post post : allPosts) {
                if (post.getTitle().toLowerCase().contains(search.toLowerCase()) || post.getContent().toLowerCase().contains(search.toLowerCase())) {
                    searchPosts.add(post);
                }
            }
        }
        searchPosts.sort((p1, p2) -> Math.toIntExact(p2.getView() - p1.getView()));
        if (searchPosts.size() == 0) {
            tvNoSearch.setVisibility(View.VISIBLE);
            tvNoSearch.setText("Không có bài viết nào");
            rvSearchPost.setVisibility(View.GONE);
        }
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(getContext(), DetailPostActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }
}