package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.PostsAdapter;
import com.tuplv.dforum.adapter.ViewPagerAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnPostClickListener {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar tbMain;
    RecyclerView rvSearchPost;
    PostsAdapter postsAdapter;
    List<Post> postsSearch;
    List<Post> posts;
    private long outApp;
    SharedPreferences sharedPreferences;

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
                        bottomNavigationView.getMenu().findItem(R.id.mnuProfile).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.mnuAdmin).setChecked(true);
                        break;
                    default:
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
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);

        posts = new ArrayList<>();
        postsSearch = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        Toast outToast = Toast.makeText(AdminMainActivity.this, "CLICK 1 lần nữa để thoát !", Toast.LENGTH_SHORT);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSearch:
                rvSearchPost.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);

                findAllPost();

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
                        loadDataToSearch(newText);
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
                        viewPager.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
                break;
            case R.id.mnuNotify:
                Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuHome:
                viewPager.setCurrentItem(0);
                break;
            case R.id.mnuProfile:
                viewPager.setCurrentItem(1);
                break;
            case R.id.mnuAdmin:
                viewPager.setCurrentItem(2);
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

        Intent intent = new Intent(this, ViewPostsActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadDataToSearch(String search) {
        postsAdapter = new PostsAdapter(this, R.layout.item_posts, postsSearch, this);
        rvSearchPost.setAdapter(postsAdapter);
        rvSearchPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        postsSearch.clear();
        if (!search.isBlank()) {
            for (Post post : posts) {
                if (post.getTitle().toLowerCase().contains(search.toLowerCase()) || post.getContent().toLowerCase().contains(search.toLowerCase())) {
                    postsSearch.add(post);
                }
            }
        }
        postsAdapter.notifyDataSetChanged();
    }

    private void findAllPost() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
}