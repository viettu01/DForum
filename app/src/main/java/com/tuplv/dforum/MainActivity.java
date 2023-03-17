package com.tuplv.dforum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.tuplv.dforum.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar tbMain;
    private long outApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setSupportActionBar(tbMain);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.mnuHome).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.mnuAddPosts).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.mnuProfile).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.mnuAdmin).setChecked(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuSearch:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Tìm kiếm ...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // Do something when the search button is pressed
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // Do something when the search text changes
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
            case R.id.mnuAddPosts:
                viewPager.setCurrentItem(1);
                break;
            case R.id.mnuProfile:
                viewPager.setCurrentItem(2);
                break;
            case R.id.mnuAdmin:
                viewPager.setCurrentItem(3);
                break;
        }
        return true;
    }
}