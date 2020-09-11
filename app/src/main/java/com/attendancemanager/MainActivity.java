package com.attendancemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.navigation.ExpandableBottomBarNavigationUI;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ExpandableBottomBar bottomBar;

    private static final String TAG = "MainActivity";

//    TODO: close db connection in onDestroy
//    https://developer.android.com/training/data-storage/sqlite#PersistingDbConnection

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem: " + position);
            switch (position) {
                case 0:
                    return new TimeTableFragment();
                case 1:
                    return new HomeFragment();
                case 2:
                    return new SettingsFragment();
            }
            throw new IllegalStateException("Unexpected Position" + position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Context context = MainActivity.this;
            switch (position) {
                case 0:
                    return context.getString(R.string.time_table);
                case 1:
                    return context.getString(R.string.home);
                case 2:
                    return context.getString(R.string.settings);
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = findViewById(R.id.bottom_bar);
        viewPager = findViewById(R.id.view_pager);

//        https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller
//        https://stackoverflow.com/questions/58703451/fragmentcontainerview-as-navhostfragment

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        NavController navController = navHostFragment.getNavController();
        ExpandableBottomBarNavigationUI.setupWithNavController(bottomBar, navController);

//        https://proandroiddev.com/the-seven-actually-10-cardinal-sins-of-android-development-491d2f64c8e0
//        3rd point

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(viewPagerAdapter);
        bottomBar.select(R.id.homeFragment);
        viewPager.setCurrentItem(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomBar.select(R.id.timeTableFragment);
                        break;

                    case 1:
                        bottomBar.select(R.id.homeFragment);
                        break;

                    case 2:
                        bottomBar.select(R.id.settingsFragment);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomBar.setOnItemSelectedListener((view, item) -> {
            Log.d(TAG, "onCreate: " + item.getItemId());
            switch (item.getItemId()) {
                case R.id.timeTableFragment:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.homeFragment:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.settingsFragment:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return null;
        });

    }
}