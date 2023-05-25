package com.tuplv.dforum.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.forum.AddAndUpdateForumActivity;
import com.tuplv.dforum.activity.post.ListPostActivity;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Forum;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment implements OnForumClickListener {

    RecyclerView rvListForum;
    RelativeLayout rlAddForum;
    SharedPreferences sharedPreferences;
    ForumAdapter forumAdapter;
    List<Forum> forums;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        init (view);
        getAllForum();

        if (!sharedPreferences.getString("role", "").equals(ROLE_ADMIN))
            rlAddForum.setVisibility(View.GONE);

        rlAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAndUpdateForumActivity.class));
            }
        });
        return view;
    }

    private void init(View view) {
        rvListForum = view.findViewById(R.id.rvListForum);
        rlAddForum = view.findViewById(R.id.rlAddForum);
        sharedPreferences = requireContext().getSharedPreferences("account", MODE_PRIVATE);
    }

    @Override
    public void goToActivityUpdate(Forum forum) {
        Intent intent = new Intent(requireContext(), AddAndUpdateForumActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
                            Toast.makeText(requireContext(), "Xóa diễn đàn thành công", Toast.LENGTH_SHORT).show();
                            forums.remove(forum);
                            forumAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(requireContext(), ListPostActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllForum() {
        forums = new ArrayList<>();
        forumAdapter = new ForumAdapter(requireContext(), R.layout.item_forum, forums, this);
        rvListForum.setAdapter(forumAdapter);
        rvListForum.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dsForum) {
                forums.clear();
                forums.add(new Forum(0, "Tất cả", ""));
                for (DataSnapshot dataSnapshot : dsForum.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
                forumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}