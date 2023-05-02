package com.tuplv.dforum.activity.notify;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_NEW_POST_NEED_APPROVE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.activity.post.DetailPostApproveActivity;
import com.tuplv.dforum.activity.post.ListPostActivity;
import com.tuplv.dforum.adapter.NotifyAdapter;
import com.tuplv.dforum.interf.OnNotifyClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_notify, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuCheckAllNotify) {
            for (Notify notify : notifies) {
                HashMap<String, Object> updateStatus = new HashMap<>();
                updateStatus.put("status", STATUS_ENABLE);
                FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId()))
                        .updateChildren(updateStatus);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllNotify() {
        if (user != null) {
            notifies = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(user.getUid()).child(OBJ_NOTIFY)
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dsNotify) {
                            notifyAdapter = new NotifyAdapter(ListNotifyActivity.this, R.layout.item_notify, notifies, ListNotifyActivity.this);
                            rvListNotify.setAdapter(notifyAdapter);
                            rvListNotify.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
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

                        if (notify.getTypeNotify().equals(TYPE_NOTIFY_NEW_POST_NEED_APPROVE)) {
                            intent = new Intent(ListNotifyActivity.this, DetailPostApproveActivity.class);

                            if (Objects.requireNonNull(post).getStatus().equals(STATUS_ENABLE)) {
                                Toast.makeText(ListNotifyActivity.this, "Bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                                intent = new Intent(ListNotifyActivity.this, DetailPostActivity.class);
                            }
                        }

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

    @Override
    public void goToListPostActivity(Notify notify) {
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(notify.getForumId()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Forum forum = snapshot.getValue(Forum.class);
                        if (forum == null) {
                            Toast.makeText(ListNotifyActivity.this, "Diễn đàn nay không còn tồn tại", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(ListNotifyActivity.this, ListPostActivity.class);
                        intent.putExtra("forum", forum);
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

    @Override
    public void onCheckNotify(Notify notify) {
        HashMap<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("status", STATUS_ENABLE);
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId()))
                .updateChildren(updateStatus);
    }

    @Override
    public void onDeleteNotify(Notify notify) {
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId()))
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notifies.remove(notify);
                            notifyAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ListNotifyActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}