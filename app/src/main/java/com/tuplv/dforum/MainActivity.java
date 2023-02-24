package com.tuplv.dforum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private long outApp;
    Toast outToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager =findViewById(R.id.viewPager);
        bottomNavigationView =findViewById(R.id.bottom_Navigation);

        ViewPagerAdapter adapter =new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_hdsd).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.menu_help).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_profile:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_hdsd:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_help:
                        viewPager.setCurrentItem(3);
                        break;
                    default:
                }
                return true;
            }
        });

    }
    @Override
    public void onBackPressed() {
        if(outApp + 2000 > System.currentTimeMillis()){
            outToast.cancel();
            super.onBackPressed();
            return;
        }else{
            outToast = Toast.makeText(MainActivity.this, "CLICK 1 lần nữa để thoát !", Toast.LENGTH_SHORT);
            outToast.show();
        }
        outApp=System.currentTimeMillis();
    }
}