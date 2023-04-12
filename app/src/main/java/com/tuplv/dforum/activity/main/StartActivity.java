package com.tuplv.dforum.activity.main;

import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tuplv.dforum.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startApplication();
    }

    private void startApplication() {
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        Timer RunSplash = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN))
                            startActivity(new Intent(StartActivity.this, AdminMainActivity.class));
                        else
                            startActivity(new Intent(StartActivity.this, UserMainActivity.class));

                        Toast.makeText(StartActivity.this, "Chào mừng bạn trở lại!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        };
        RunSplash.schedule(timerTask, SPLASH_TIME_OUT);
    }
}