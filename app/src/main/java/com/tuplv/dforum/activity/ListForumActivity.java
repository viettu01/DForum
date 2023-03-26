package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_FORUM;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.model.Forum;

import java.util.ArrayList;
import java.util.List;

public class ListForumActivity extends AppCompatActivity {

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

        loadDataToView();
    }

    private void init() {
        tbListForum = findViewById(R.id.tbListForum);
        rvListForum = findViewById(R.id.rvListForum);
    }

    private void loadDataToView() {
        forums = new ArrayList<>();
        forumAdapter = new ForumAdapter(ListForumActivity.this, R.layout.item_list_forum, forums);
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