package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostsActivity extends AppCompatActivity {

    Toolbar tbViewPosts;
    CircleImageView imvAvatar;
    TextView tvNamePoster, tvDatePost, tvTitlePost, tvContentPosts;
    RecyclerView rvComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);
        init();
        tbViewPosts.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadData();
    }

    private void init() {
        tbViewPosts = findViewById(R.id.tbViewPosts);
        setSupportActionBar(tbViewPosts);
        imvAvatar = findViewById(R.id.imvAvatar);
        tvNamePoster = findViewById(R.id.tvNamePoster);
        tvDatePost = findViewById(R.id.tvDatePost);
        tvTitlePost = findViewById(R.id.tvTitlePost);
        tvContentPosts = findViewById(R.id.tvContentPosts);
        rvComment = findViewById(R.id.rvComment);
    }

    @SuppressLint("SimpleDateFormat")
    private void loadData() {
        Post post = (Post) getIntent().getSerializableExtra("post");

        if (post != null) {
            FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(post.getAccountId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Account account = snapshot.getValue(Account.class);
                                if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                    imvAvatar.setImageResource(R.drawable.no_avatar);
                                else
                                    Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
                                tvNamePoster.setText(account.getNickName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
            tvDatePost.setText(dateFormat.format(new Date(post.getApprovalDate())));
            tvTitlePost.setText(post.getTitle());
            tvContentPosts.setText(post.getContent());
        }
    }
}