package com.tuplv.dforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuplv.dforum.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar tbMain;
    private long outApp;
    Toast outToast;


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
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                    default:
                }
                return true;
            }
        });
    }

    private void init() {
        tbMain = findViewById(R.id.tbMain);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    @Override
    public void onBackPressed() {
        if (outApp + 2000 > System.currentTimeMillis()) {
            outToast.cancel();
            super.onBackPressed();
            return;
        } else {
            outToast = Toast.makeText(MainActivity.this, "CLICK 1 lần nữa để thoát !", Toast.LENGTH_SHORT);
            outToast.show();
        }
        outApp = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}