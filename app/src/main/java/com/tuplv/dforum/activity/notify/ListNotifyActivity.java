package com.tuplv.dforum.activity.notify;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.adapter.NotifyAdapter;
import com.tuplv.dforum.interf.OnNotifyClickListener;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ListNotifyActivity extends AppCompatActivity implements OnNotifyClickListener {

    Toolbar tbListNotify;
    RecyclerView rvListNotify;
    List<Notify> notifies;
    NotifyAdapter notifyAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notify);
        init();

        tbListNotify.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getAllNotify();
    }

    private void init() {
        tbListNotify = findViewById(R.id.tbListNotify);
        setSupportActionBar(tbListNotify);
        rvListNotify = findViewById(R.id.rvListNotify);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllNotify() {
        if (user != null) {
            notifies = new ArrayList<>();
            notifyAdapter = new NotifyAdapter(this, R.layout.item_notify, notifies, this);
            rvListNotify.setAdapter(notifyAdapter);
            rvListNotify.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

            FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(user.getUid()).child(OBJ_NOTIFY)
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dsNotify) {
                            notifies.clear();
                            for (DataSnapshot dataSnapshot : dsNotify.getChildren()) {
                                Notify notify = dataSnapshot.getValue(Notify.class);
                                notifies.add(notify);
                            }
                            Collections.reverse(notifies);
                            notifyAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            notifyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void goToDetailPostActivity(Notify notify) {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(notify.getPostId()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Post post = snapshot.getValue(Post.class);
                        Intent intent = new Intent(ListNotifyActivity.this, DetailPostActivity.class);
                        intent.putExtra("post", post);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("status", STATUS_ENABLE);
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(user.getUid())
                .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId()))
                .updateChildren(updateView);
    }
}