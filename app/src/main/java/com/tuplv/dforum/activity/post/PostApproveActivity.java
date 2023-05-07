package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.STATUS_NO_APPROVE_POST;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_APPROVE_POST;
import static com.tuplv.dforum.until.Until.sendNotifyToAuthor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostApproveClickListener;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostApproveActivity extends AppCompatActivity implements OnPostApproveClickListener {
    Toolbar tbPostApprove;
    RecyclerView rvListPostApprove;
    AlertDialog.Builder builder;
    List<Post> posts;
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_approve);
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
        getAllPostApprove();
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

        builder = new AlertDialog.Builder(PostApproveActivity.this);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllPostApprove() {
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(PostApproveActivity.this, R.layout.item_post_approve, posts, this);
        rvListPostApprove.setAdapter(postAdapter);
        rvListPostApprove.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("status").equalTo(STATUS_DISABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            posts.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void goToActivityDetail(Post post) {
        Intent intent = new Intent(this, DetailPostApproveActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    @Override
    public void approvePost(Post post) {
        builder.setTitle("Thông báo!");
        builder.setIcon(R.drawable.ic_round_info_24);
        builder.setMessage("Bạn có chắc chắn muốn duyệt bài viết này?");
        builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> updateView = new HashMap<>();
                updateView.put("status", STATUS_ENABLE);
                updateView.put("approveDate", new Date().getTime());
                FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                        .updateChildren(updateView);
                Toast.makeText(PostApproveActivity.this, "Bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                posts.clear();
                getAllPostApprove();
                sendNotifyToAuthor(post, TYPE_NOTIFY_APPROVE_POST,null);
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
    public void noApprovePost(Post post) {
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_round_warning_yellow_24);
        builder.setMessage("Bạn có chắc chắn không phê duyệt bài viết này?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId())).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PostApproveActivity.this, "Bài viết không được phê duyệt", Toast.LENGTH_SHORT).show();
                                        getAllPostApprove();
                                    } else
                                        Toast.makeText(PostApproveActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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

//        dialogNoApprove(post);
    }

    // dialog no approve post
    private void dialogNoApprove(Post post) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_approve_post);

        // Không cho thoát khi bấm ra ngoài màn hình
        dialog.setCanceledOnTouchOutside(false);

        //ánh xạ
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtReason = dialog.findViewById(R.id.edtReason);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = edtReason.getText().toString().trim();
                if (reason.isEmpty())
                    Toast.makeText(PostApproveActivity.this, "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
                else {
                    HashMap<String, Object> updateView = new HashMap<>();
                    updateView.put("status", STATUS_NO_APPROVE_POST);
                    updateView.put("approveDate", new Date().getTime());
                    FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                            .updateChildren(updateView);
                    Toast.makeText(PostApproveActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    getAllPostApprove();
                    sendNotifyToAuthor(post, " (Admin) không duyệt bài viết của bạn vì lý do: " + reason, null);
                    dialog.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuPostApprove) {
            builder.setTitle("Cảnh báo!");
            builder.setIcon(R.drawable.ic_round_warning_yellow_24);
            builder.setMessage("Bạn có chắc chắn muốn duyệt tất cả bài viết?");
            builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> updateView = new HashMap<>();
                    updateView.put("status", STATUS_ENABLE);
                    updateView.put("approveDate", new Date().getTime());
                    for (Post post : posts) {
                        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                                .updateChildren(updateView);
                        sendNotifyToAuthor(post, TYPE_NOTIFY_APPROVE_POST,null);
                    }
                    Toast.makeText(PostApproveActivity.this, "Tất cả bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                    getAllPostApprove();
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
