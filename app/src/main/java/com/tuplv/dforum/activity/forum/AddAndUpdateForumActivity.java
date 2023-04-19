package com.tuplv.dforum.activity.forum;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_POST;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.Date;
import java.util.Objects;

public class AddAndUpdateForumActivity extends AppCompatActivity {

    EditText edtNameForum, edtDesForum;
    Button btnAddForum;
    ProgressBar pbForum;
    Toolbar tbAddNewForum;
    Forum forum;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);

        init();

        tbAddNewForum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameForum.getText().toString().isEmpty()) {
                    Toast.makeText(AddAndUpdateForumActivity.this, "Vui lòng nhập tên diễn đàn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pbForum.setVisibility(View.VISIBLE);
                if (forum != null)
                    updateForum();
                else
                    addForum();
                finish();
            }
        });

        checkUpdate();
    }

    public void init() {
        edtNameForum = findViewById(R.id.edtNameForum);
        edtDesForum = findViewById(R.id.edtDesForum);
        btnAddForum = findViewById(R.id.btnShowListForum);
        tbAddNewForum = findViewById(R.id.tbAddNewForum);
        setSupportActionBar(tbAddNewForum);
        pbForum = findViewById(R.id.pbForum);
    }

    public void addForum() {
        forum = new Forum();
        forum.setForumId(new Date().getTime());
        forum.setName(edtNameForum.getText().toString());
        forum.setDescription(edtDesForum.getText().toString());
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(AddAndUpdateForumActivity.this, "Thêm diễn đàn thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddAndUpdateForumActivity.this, "Thêm diễn đàn thất bại", Toast.LENGTH_SHORT).show();
                        pbForum.setVisibility(View.GONE);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void checkUpdate() {
        forum = (Forum) getIntent().getSerializableExtra("forum");
        if (forum != null) {
            tbAddNewForum.setTitle("Cập nhât diễn đàn");
            btnAddForum.setText("Cập nhật");
            edtNameForum.setText(forum.getName());
            edtDesForum.setText(forum.getDescription());
        }
    }

    public void updateForum() {
        forum.setName(edtNameForum.getText().toString());
        forum.setDescription(edtDesForum.getText().toString());
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(AddAndUpdateForumActivity.this, "Cập nhật diễn đàn thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddAndUpdateForumActivity.this, "Cập nhật diễn đàn thất bại", Toast.LENGTH_SHORT).show();
                        pbForum.setVisibility(View.GONE);
                    }
                });
    }

}