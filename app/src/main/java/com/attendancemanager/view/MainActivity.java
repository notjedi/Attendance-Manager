package com.attendancemanager.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.attendancemanager.R;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_SETTINGS_FILE_KEY = "extra_settings";
    public static final String SHARED_PREFS_ATTENDANCE_CRITERIA = "attendance_criteria";
    public static final String SHARED_PREFS_STATUS_LAST_UPDATED = "status_last_updated";
    public static final String SHARED_PREFS_EXTRA_LAST_ADDED = "extra_subject_last_added";
    public static final String SHARED_PREFS_IS_FIRST_RUN = "is_first_run";
    private ViewPager mViewPager;
    private ExpandableBottomBar mBottomBar;
    private final Runnable hideBottomBar = new Runnable() {
        @Override
        public void run() {
            mBottomBar.hide();
        }
    };
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);

        /* Find views */
        mBottomBar = findViewById(R.id.bottom_bar);
        mViewPager = findViewById(R.id.view_pager);

        initialSetup();
        setupViewPager();
    }

    private void initialSetup() {
        /* All initializing stuff */

        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(hideBottomBar, 2000);

        /*
        *  Window window = getWindow();
        * window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // Prevents the bottom bar from coming up along with the keyboard
        * window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        */

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPinkPrimaryDark));
        /* Set to light mode by preventing the app to follow system wide default */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @SuppressLint("NonConstantResourceId")
    private void setupViewPager() {
        /* Initializes the view pager */

        /* https://proandroiddev.com/the-seven-actually-10-cardinal-sins-of-android-development-491d2f64c8e0
        3rd point */
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerAdapter);
        mBottomBar.select(R.id.homeFragment);
        mViewPager.setCurrentItem(1);

        /* Listen for page changes and select change the bottom bar accordingly */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /* Required */
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomBar.select(R.id.timeTableFragment);
                        removeAndShowBottomBar();
                        mHandler.postDelayed(() -> mBottomBar.hide(), 1000);
                        break;
                    case 1:
                        mBottomBar.select(R.id.homeFragment);
                        removeAndShowBottomBar();
                        mHandler.postDelayed(hideBottomBar, 3000);
                        break;
                    case 2:
                        mBottomBar.select(R.id.settingsFragment);
                        removeAndShowBottomBar();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /* Required */
            }
        });

        /* Listen for item selections and change the view pager accordingly */
        mBottomBar.setOnItemSelectedListener((view, item) -> {
            switch (item.getItemId()) {
                case R.id.timeTableFragment:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.homeFragment:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.settingsFragment:
                    mViewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }
            return null;
        });
    }

    private void removeAndShowBottomBar() {
        mHandler.removeCallbacks(hideBottomBar);
        mBottomBar.show();
    }

    @Override
    public void onUserInteraction() {
        /* Hide the bottom bar after 3 seconds of inactivity only when currentTab = HomeFragment */

        super.onUserInteraction();
        if (mViewPager.getCurrentItem() == 1) {
            mHandler.removeCallbacks(hideBottomBar);
            mBottomBar.show();
            mHandler.postDelayed(hideBottomBar, 2000);
        }
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TimeTableFragment.getInstance();
                case 1:
                    return HomeFragment.getInstance();
                case 2:
                    return SettingsFragment.getInstance();
                default:
                    throw new IllegalStateException("Unexpected Position" + position);
            }
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
                default:
                    return null;
            }
        }
    }
}