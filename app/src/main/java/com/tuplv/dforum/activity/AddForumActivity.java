package com.tuplv.dforum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuplv.dforum.R;

public class AddForumActivity extends AppCompatActivity {

    EditText edtContentForum, edtDesForum;
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
    }

    public void init() {
        edtContentForum = findViewById(R.id.edtContentForum);
        edtDesForum = findViewById(R.id.edtDesForum);
        btnAddForum = findViewById(R.id.btnAddForum);
        tbAddNewForum = findViewById(R.id.tbAddNewForum);
    }
}