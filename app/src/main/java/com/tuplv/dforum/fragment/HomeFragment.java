package com.tuplv.dforum.fragment;

import static com.tuplv.dforum.activity.main.StartActivity.forumIds;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.forum.AddAndUpdateForumActivity;
import com.tuplv.dforum.activity.post.AddPostActivity;
import com.tuplv.dforum.activity.post.ListPostActivity;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Forum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, OnForumClickListener {

    RecyclerView rvListForumFeatured;
    FloatingActionButton fabAddPost;
    ForumAdapter forumAdapter;
    List<Forum> forums;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);


        return view;
    }

    private void init(View view) {
        rvListForumFeatured = view.findViewById(R.id.rvListForumFeatured);
        fabAddPost = view.findViewById(R.id.fabAddPost);

        fabAddPost.setOnClickListener(this);

        forums = new ArrayList<>();
    }

    // Hiển thị 5 danh sách diễn đàn mới nhất
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
        if (view.getId() == R.id.fabAddPost) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                startActivity(new Intent(getActivity(), AddPostActivity.class));
            else
                Toast.makeText(getActivity(), "Đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getListForumFeatured();
//        getListForumById();
    }

    @Override
    public void goToActivityUpdate(Forum forum) {
        Intent intent = new Intent(getActivity(), AddAndUpdateForumActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Cảnh báo!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc chắn muốn xóa diễn đàn này?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId()));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Xóa diễn đàn thành công", Toast.LENGTH_SHORT).show();
                            forums.remove(forum);
                            forumAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void goToListPostOfForum(Forum forum) {
        Intent intent = new Intent(getActivity(), ListPostActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }
}