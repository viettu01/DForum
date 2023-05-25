package com.tuplv.dforum.activity.main;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.notify.ListNotifyActivity;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.adapter.CommentAdapter;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.adapter.ViewPagerAdapter;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnPostClickListener, OnCommentClickListener {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    Toolbar tbMain;
    TextView tvCardBadge;
    RecyclerView rvSearchPost;
    RecyclerView rvSearchComment;
    PostAdapter postAdapter;
    CommentAdapter commentsAdapter;
    List<Post> postsSearch;
    List<Comment> commentsSearch;
    List<Post> posts;
    List<Comment> comments;
    private long outApp;
    SharedPreferences sharedPreferences;
    int countNotify = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setSupportActionBar(tbMain);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        tbMain.setVisibility(View.GONE);
                        bottomNavigationView.getMenu().findItem(R.id.mnuSearch).setChecked(true);
                        break;
                    case 2:
                        tbMain.setVisibility(View.GONE);
                        bottomNavigationView.getMenu().findItem(R.id.mnuForum).setChecked(true);
                        break;
                    case 3:
                        tbMain.setVisibility(View.GONE);
                        bottomNavigationView.getMenu().findItem(R.id.mnuProfile).setChecked(true);
                        break;
                    case 0:
                    default:
                        tbMain.setVisibility(View.VISIBLE);
                        bottomNavigationView.getMenu().findItem(R.id.mnuHome).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private void init() {
        tbMain = findViewById(R.id.tbMain);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        rvSearchPost = findViewById(R.id.rvSearchPost);
        rvSearchComment = findViewById(R.id.rvSearchComment);
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);

        posts = new ArrayList<>();
        comments = new ArrayList<>();
        postsSearch = new ArrayList<>();
        commentsSearch = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        Toast outToast = Toast.makeText(MainActivity.this, "CLICK 1 lần nữa để thoát !", Toast.LENGTH_SHORT);
        if (outApp + 2000 > System.currentTimeMillis()) {
            outToast.cancel();
            super.onBackPressed();
            return;
        } else {
            outToast.show();
        }
        outApp = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeNotify();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.mnuNotify);
        View view = menuItem.getActionView();
        tvCardBadge = view.findViewById(R.id.tvNotifyBadge);
        setupBadge();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSearch:
                rvSearchPost.setVisibility(View.VISIBLE);
                rvSearchComment.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);

                getAllPost();
                getAllComment();

                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Tìm kiếm ...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // Do something when the search button is pressed
                        return true;
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        postsSearch.clear();
                        searchPost(newText);

                        commentsSearch.clear();
                        searchComment(newText);
                        return true;
                    }
                });

                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                        rvSearchPost.setVisibility(View.GONE);
                        rvSearchComment.setVisibility(View.GONE);
                        viewPager.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
                break;
            case R.id.mnuNotify:
                if (FirebaseAuth.getInstance().getCurrentUser() == null)
                    Toast.makeText(this, "Đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(MainActivity.this, ListNotifyActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuHome:
                tbMain.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(0);
                break;
            case R.id.mnuSearch:
                tbMain.setVisibility(View.GONE);
                viewPager.setCurrentItem(1);
                break;
            case R.id.mnuForum:
                tbMain.setVisibility(View.GONE);
                viewPager.setCurrentItem(2);
                break;
            case R.id.mnuProfile:
                tbMain.setVisibility(View.GONE);
                viewPager.setCurrentItem(3);
                break;
        }
        return true;
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(this, DetailPostActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            posts.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllComment() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot commentSnapshot : postSnapshot.child(OBJ_COMMENT).getChildren()) {
                                Comment comment = commentSnapshot.getValue(Comment.class);
                                if (comment != null)
                                    comments.add(comment);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchComment(String search) {
        commentsAdapter = new CommentAdapter(this, R.layout.item_comment, commentsSearch, this);
        rvSearchComment.setAdapter(commentsAdapter);
        rvSearchComment.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        if (!search.isBlank()) {
            for (Comment comment : comments) {
                if (comment.getContent().toLowerCase().contains(search.toLowerCase())) {
                    commentsSearch.add(comment);
                }
            }
        }
        commentsAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void searchPost(String search) {
        postAdapter = new PostAdapter(this, R.layout.item_post, postsSearch, this);
        rvSearchPost.setAdapter(postAdapter);
        rvSearchPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        if (!search.isBlank()) {
            for (Post post : posts) {
                if (post.getTitle().toLowerCase().contains(search.toLowerCase()) || post.getContent().toLowerCase().contains(search.toLowerCase())) {
                    postsSearch.add(post);
                }
            }
        }
        postAdapter.notifyDataSetChanged();
    }

    private void setupBadge() {
        if (tvCardBadge != null) {
            if (countNotify == 0) {
                if (tvCardBadge.getVisibility() != View.GONE) {
                    tvCardBadge.setVisibility(View.GONE);
                }
            } else {
                tvCardBadge.setText(String.valueOf(Math.min(countNotify, 99)));
                if (tvCardBadge.getVisibility() != View.VISIBLE) {
                    tvCardBadge.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void changeNotify() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(user.getUid()).child(OBJ_NOTIFY)
                    .orderByChild("status").equalTo(STATUS_DISABLE)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            countNotify = (int) snapshot.getChildrenCount();
                            setupBadge();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public void goToActivityUpdate(Comment comment) {

    }

    @Override
    public void onDeleteClick(Comment comment) {

    }

    @Override
    public void goToActivityComment(Comment comment, String nameAuthorRepComment) {

    }
}