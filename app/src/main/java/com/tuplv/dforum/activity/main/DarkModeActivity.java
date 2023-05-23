package com.tuplv.dforum.activity.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuplv.dforum.R;

public class DarkModeActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar tbDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dark_mode);

        init();

        tbDarkMode.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        tbDarkMode = findViewById(R.id.tbDarkMode);
        setSupportActionBar(tbDarkMode);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnCancel:
//                finish();
//                break;
//            case R.id.btnUpdateName:
//                checkSameName();
//                break;
//        }
    }
}