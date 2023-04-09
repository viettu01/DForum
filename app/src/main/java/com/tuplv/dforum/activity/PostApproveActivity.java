package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.ForumAdapter;
import com.tuplv.dforum.adapter.PostsAdapter;
import com.tuplv.dforum.interf.OnPostApproveClickListener;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostApproveActivity extends AppCompatActivity implements OnPostApproveClickListener {
    Toolbar tbPostApprove;
    RecyclerView rvListPostApprove;
    List<Post> posts;
    PostsAdapter postsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_approve_main);
        init();


        tbPostApprove.setNavigationOnClickListener(new View.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_approve_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        tbPostApprove = findViewById(R.id.tbPostApprove);
        setSupportActionBar(tbPostApprove);
        rvListPostApprove = findViewById(R.id.rvListPostApprove);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataToView() {
        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(PostApproveActivity.this, R.layout.item_post_approve, posts, this);
        rvListPostApprove.setAdapter(postsAdapter);
        rvListPostApprove.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("status").equalTo(STATUS_DISABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            posts.add(post);
                        }
                        postsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void goToActivityDetail(Post post) {
        Intent intent = new Intent(this, ViewPostApproveActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    @Override
    public void postApprove(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostApproveActivity.this);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc chắn muốn duyệt bài viết này?");
        builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> updateView = new HashMap<>();
                updateView.put("status", STATUS_ENABLE);
                FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                        .updateChildren(updateView);
                Toast.makeText(PostApproveActivity.this, "Bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                posts.clear();
                loadDataToView();
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
    public void noPostApprove(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostApproveActivity.this);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc chắn không phê duyệt bài viết này?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostApproveActivity.this, "Bài viết không được phê duyệt", Toast.LENGTH_SHORT).show();
                            posts.clear();
                            loadDataToView();
                        } else {
                            Toast.makeText(PostApproveActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuPostApprove) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PostApproveActivity.this);
            builder.setTitle("Cảnh báo!");
            builder.setIcon(android.R.drawable.ic_delete);
            builder.setMessage("Bạn có chắc chắn muốn duyệt tất cả bài viết?");
            builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> updateView = new HashMap<>();
                    updateView.put("status", STATUS_ENABLE);
                    for (Post post : posts) {
                        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                                .updateChildren(updateView);
                    }
                    Toast.makeText(PostApproveActivity.this, "Tất cả bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                    posts.clear();
                    loadDataToView();
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
