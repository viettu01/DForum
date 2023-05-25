package com.tuplv.dforum.activity.main;

import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000;
    SharedPreferences sharedPreferences;
    ImageView logo;
    public static List<Long> forumIds;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        logo = findViewById(R.id.logo);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            logo.setImageResource(R.drawable.logo_white);
        } else {
            logo.setImageResource(R.drawable.applogoblue);
        }


        forumIds = new ArrayList<>();
        getListIdForum();
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
                        Intent intent;
                        intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(StartActivity.this, "Chào mừng bạn trở lại!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        };
        RunSplash.schedule(timerTask, SPLASH_TIME_OUT);
    }

    private void getListIdForum() {
        HashMap<Long, Integer> forumCountPost = new HashMap<>();
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        forumIds.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (forumCountPost.containsKey(Objects.requireNonNull(post).getForumId())) {
                                int postCount = forumCountPost.get(post.getForumId()) + 1;
                                forumCountPost.put(post.getForumId(), postCount);
                            } else {
                                forumCountPost.put(post.getForumId(), 1);
                            }
                        }

                        // Sắp xếp HashMap theo giá trị số lượng bài viết giảm dần
                        List<Map.Entry<Long, Integer>> sortedForumList = new ArrayList<>(forumCountPost.entrySet());
                        sortedForumList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                        // Lấy ra top 5 diễn đàn
                        List<Map.Entry<Long, Integer>> topEntries = sortedForumList.subList(0, Math.min(5, sortedForumList.size()));

                        for (Map.Entry<Long, Integer> top : topEntries) {
                            forumIds.add(top.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}