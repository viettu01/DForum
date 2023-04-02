package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_FORUM;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Forum;

import java.util.ArrayList;
import java.util.List;

public class ListForumActivity extends AppCompatActivity implements OnForumClickListener {

    Toolbar tbListForum;
    RecyclerView rvListForum;
    ForumAdapter forumAdapter;
    List<Forum> forums;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_forum);

        init();
        setSupportActionBar(tbListForum);

        tbListForum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataToView();
    }

    @Override
    public void goToActivityUpdate(Forum forum) {
        Intent intent = new Intent(this, AddAndUpdateForumActivity.class);
        intent.putExtra("forum", forum);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                            Toast.makeText(ListForumActivity.this, "Xóa diễn đàn thành công", Toast.LENGTH_SHORT).show();
                            forums.remove(forum);
                            forumAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ListForumActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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

    private void init() {
        tbListForum = findViewById(R.id.tbListForum);
        rvListForum = findViewById(R.id.rvListForum);
    }

    private void loadDataToView() {
        forums = new ArrayList<>();
        forumAdapter = new ForumAdapter(ListForumActivity.this, R.layout.item_forum, forums, this);
        rvListForum.setAdapter(forumAdapter);
        rvListForum.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
                forumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListForumActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}