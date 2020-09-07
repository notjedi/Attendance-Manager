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


import java.util.ArrayList;
import java.util.List;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.navigation.ExpandableBottomBarNavigationUI;

public class MainActivity extends AppCompatActivity {

    private FragmentContainerView fragmentContainer;
    private ViewPager viewPager;
    private ExpandableBottomBar bottomBar;

    private TimeTableFragment timeTableFragment;
    private HomeFragment homeFragment;
    private SettingsFragment settingsFragment;

    private static final String TAG = "MainActivity";


    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {

            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = findViewById(R.id.bottom_bar);
        viewPager = findViewById(R.id.view_pager);
        fragmentContainer = findViewById(R.id.fragment_container);

        timeTableFragment = new TimeTableFragment();
        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();

//        https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller
//        https://stackoverflow.com/questions/58703451/fragmentcontainerview-as-navhostfragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        NavController navController = navHostFragment.getNavController();
        ExpandableBottomBarNavigationUI.setupWithNavController(bottomBar, navController);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(timeTableFragment, "Time Table");
        viewPagerAdapter.addFragment(homeFragment, "Home");
        viewPagerAdapter.addFragment(settingsFragment, "Settings");
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