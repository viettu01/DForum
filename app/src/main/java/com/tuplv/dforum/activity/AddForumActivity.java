package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_FORUM;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Forum;

import java.util.Date;

public class AddForumActivity extends AppCompatActivity {

    EditText edtNameForum, edtDesForum;
    Button btnAddForum;
    Toolbar tbAddNewForum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);

        init();
        setSupportActionBar(tbAddNewForum);

        tbAddNewForum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Forum forum = new Forum();
                forum.setForumId(new Date().getTime());
                forum.setName(edtNameForum.getText().toString());
                forum.setDescription(edtDesForum.getText().toString());
                create(forum);
                finish();
            }
        });

    }

    public void init() {
        edtNameForum = findViewById(R.id.edtNameForum);
        edtDesForum = findViewById(R.id.edtDesForum);
        btnAddForum = findViewById(R.id.btnAddForum);
        tbAddNewForum = findViewById(R.id.tbAddNewForum);
    }

    public void create(Forum forum) {
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).child(String.valueOf(forum.getForumId())).setValue(forum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(AddForumActivity.this, "Thêm forum thành công", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddForumActivity.this, "Thêm forum thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}