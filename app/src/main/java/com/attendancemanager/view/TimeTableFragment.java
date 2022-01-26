package com.attendancemanager.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.attendancemanager.R;
import com.attendancemanager.adapters.BottomSheetAdapter;
import com.attendancemanager.model.TimeTableSubject;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Date;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class TimeTableFragment extends Fragment {

    public static final String[] DAY_NAMES = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};
    public static MutableLiveData<Boolean> isEditable = new MutableLiveData<>(false);
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ExtendedFloatingActionButton mAddFab;
    private FloatingActionButton mEditFab;
    private RecyclerView mBottomSheetRecyclerView;
    private LinearLayout mBottomSheetLayout;

    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private TimeTableViewPagerAdapter pagerAdapter;
    private BottomSheetAdapter bottomSheetAdapter;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior bottomSheetBehavior;

    public static TimeTableFragment getInstance() {
        return new TimeTableFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bottomSheetAdapter = new BottomSheetAdapter();
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.time_table_toolbar);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.day_view_pager);
        mEditFab = view.findViewById(R.id.floatingActionButton);
        mAddFab = view.findViewById(R.id.add_extended_fab);
        mBottomSheetLayout = view.findViewById(R.id.bottom_sheet_constraint_layout);
        mBottomSheetRecyclerView = view.findViewById(R.id.bottom_sheet_recycler_view);

        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> bottomSheetAdapter.submitList(subjects));

        toolbar.setTitleTextAppearance(getContext(), R.style.RubixFontStyle);

        mEditFab.setOnClickListener(v -> {
            if (mAddFab.getVisibility() == View.VISIBLE) {
                /* not Editable */
                mAddFab.setVisibility(View.GONE);
                mEditFab.setImageResource(R.drawable.ic_edit);
                isEditable.setValue(false);
            } else {
                /* Editable */
                mAddFab.setVisibility(View.VISIBLE);
                mEditFab.setImageResource(R.drawable.ic_check);
                isEditable.setValue(true);
            }
        });

        mAddFab.setOnClickListener(v -> {
            if (bottomSheetAdapter.getItemCount() == 0) {
                Toast.makeText(getContext(), "Add a few subjects", Toast.LENGTH_SHORT).show();
                return;
            }
            mBottomSheetLayout.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mAddFab.setVisibility(View.GONE);
            mEditFab.setVisibility(View.GONE);
            getActivity().findViewById(R.id.bottom_bar).setVisibility(View.INVISIBLE);
        });

        setupViewPager();
        buildBottomSheet();
    }

    private void setupViewPager() {

        pagerAdapter = new TimeTableViewPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        /* 0 represents SUNDAY in the Calender class so doing some calculations to make 0 as MONDAY */
        mViewPager.setCurrentItem((calendar.get(Calendar.DAY_OF_WEEK) + 12) % 7);
        mViewPager.setOffscreenPageLimit(1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildBottomSheet() {

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mAddFab.setVisibility(View.VISIBLE);
                    mEditFab.setVisibility(View.VISIBLE);
                    ExpandableBottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);
                    bottomBar.setVisibility(View.VISIBLE);
                    bottomBar.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                /* Required */
            }
        });

        bottomSheetAdapter.setOnAddButtonClickListener(position -> {
            TimeTableSubject subject = new TimeTableSubject(
                    bottomSheetAdapter.getSubjectAt(position).getSubjectName(),
                    TimeTableSubject.NONE,
                    pagerAdapter.getPageTitle(mViewPager.getCurrentItem()).toString());
            dayViewModel.insert(subject);
        });

        mBottomSheetRecyclerView.setAdapter(bottomSheetAdapter);
        mBottomSheetRecyclerView.setHasFixedSize(true);
        mBottomSheetRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBottomSheetRecyclerView.setOnTouchListener((v, event) -> {
            /* Request parent to disallow touch event to prevent the bottom bar form popping up onTouchEvent */
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public void onDestroy() {
        // Avoid memory leak
        mAddFab = null;
        bottomSheetAdapter = null;
        mBottomSheetRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    private static class TimeTableViewPagerAdapter extends FragmentPagerAdapter {

        public TimeTableViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance(DAY_NAMES[position]);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return DAY_NAMES[position];
        }

        @Override
        public int getCount() {
            return DAY_NAMES.length;
        }
    }
}