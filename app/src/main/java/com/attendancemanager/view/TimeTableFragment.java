package com.attendancemanager.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.attendancemanager.R;
import com.attendancemanager.adapters.BottomSheetAdapter;
import com.attendancemanager.adapters.TimeTableFragmentAdapter;
import com.attendancemanager.model.Friday;
import com.attendancemanager.model.Monday;
import com.attendancemanager.model.Saturday;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.Sunday;
import com.attendancemanager.model.Thursday;
import com.attendancemanager.model.Tuesday;
import com.attendancemanager.model.Wednesday;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeTableFragment extends Fragment {

    public static final String[] DAY_NAMES = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};
    private static final String TAG = "TimeTableFragment";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExtendedFloatingActionButton addButtonFab;
    private FloatingActionButton floatingActionButton;
    private RecyclerView mBottomSheetRecyclerView;
    private LinearLayout mBottomSheetLayout;
    private List<Subject> mAllSubjectList;
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private TimeTableViewPagerAdapter pagerAdapter;
    private BottomSheetAdapter mBottomSheetAdapter;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior mBottomSheetBehavior;

    public TimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.time_table_toolbar);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.day_view_pager);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        addButtonFab = view.findViewById(R.id.add_extended_fab);
        mBottomSheetLayout = view.findViewById(R.id.bottom_sheet_constraint_layout);
        mBottomSheetRecyclerView = view.findViewById(R.id.bottom_sheet_recycler_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBottomSheetAdapter = new BottomSheetAdapter();
        mAllSubjectList = new ArrayList<>();
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> {
            mAllSubjectList.clear();
            mAllSubjectList.addAll(subjects);
            mBottomSheetAdapter.submitList(subjects);
        });

        toolbar.setTitleTextAppearance(getContext(), R.style.RubixFontStyle);

        floatingActionButton.setOnClickListener(v -> {
            if (addButtonFab.getVisibility() == View.VISIBLE) {
                addButtonFab.setVisibility(View.GONE);
                floatingActionButton.setImageResource(R.drawable.ic_edit);
            } else {
                addButtonFab.setVisibility(View.VISIBLE);
                floatingActionButton.setImageResource(R.drawable.ic_check);
            }
        });

        addButtonFab.setOnClickListener(v -> {
            mBottomSheetLayout.setVisibility(View.VISIBLE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            addButtonFab.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
        });

        setupViewPager();
        buildBottomSheet();

    }

    private void setupViewPager() {

        pagerAdapter = new TimeTableViewPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        /* TODO make a static method in MainActivity that gets the day of week and use it in all occurrences */
        /* 0 represents SUNDAY in the Calender class so doing some calculations to make 0 as MONDAY */
        viewPager.setCurrentItem((calendar.get(Calendar.DAY_OF_WEEK) + 12) % 7);
        viewPager.setOffscreenPageLimit(3);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildBottomSheet() {

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    addButtonFab.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mBottomSheetAdapter.setOnAddButtonClickListener(position -> {
            viewPager.getCurrentItem();
            dayViewModel.insert(mAllSubjectList.get(position).getSubjectName(), pagerAdapter.
                    getPageTitle(viewPager.getCurrentItem()).toString());
        });

        mBottomSheetRecyclerView.setAdapter(mBottomSheetAdapter);
        mBottomSheetRecyclerView.setHasFixedSize(true);
        mBottomSheetRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBottomSheetRecyclerView.setOnTouchListener((v, event) -> {
            /* Request parent to disallow touch event to prevent the bottom bar form popping up onTouchEvent */
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });
    }

    public static class DayFragment extends Fragment {

        private static final String ARG_DAY_NAME = "argDayName";
        private RecyclerView timeTableRecyclerView;
        private TimeTableFragmentAdapter timeTableAdapter;
        private DayViewModel dayViewModel;
        private SubjectViewModel subjectViewModel;
        private List<Subject> daySubjectList;
        private String getArgDayName;

        public static DayFragment newInstance(String day) {
            /* Create new instance with the ARG_DAY_NAME */

            DayFragment dayFragment = new DayFragment();
            final Bundle args = new Bundle();
            args.putString(ARG_DAY_NAME, day);
            dayFragment.setArguments(args);
            return dayFragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getArgDayName = getArguments().getString(ARG_DAY_NAME);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_day, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            timeTableRecyclerView = view.findViewById(R.id.time_table_recycler_view);
            dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
            subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            daySubjectList = new ArrayList<>();
            timeTableAdapter = new TimeTableFragmentAdapter();
            timeTableRecyclerView.setAdapter(timeTableAdapter);
            timeTableRecyclerView.setHasFixedSize(true);

            switch (getArgDayName.toLowerCase()) {
                case "monday":
                    dayViewModel.getMondayList().observe(getViewLifecycleOwner(), mondays -> {
                        daySubjectList.clear();
                        for (Monday monday : mondays) {
                            daySubjectList.add(subjectViewModel.getSubject(monday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "tuesday":
                    dayViewModel.getTuesdayList().observe(getViewLifecycleOwner(), tuesdays -> {
                        daySubjectList.clear();
                        for (Tuesday tuesday : tuesdays) {
                            daySubjectList.add(subjectViewModel.getSubject(tuesday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "wednesday":
                    dayViewModel.getWednesdayList().observe(getViewLifecycleOwner(), wednesdays -> {
                        daySubjectList.clear();
                        for (Wednesday wednesday : wednesdays) {
                            daySubjectList.add(subjectViewModel.getSubject(wednesday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "thursday":
                    dayViewModel.getThursdayList().observe(getViewLifecycleOwner(), thursdays -> {
                        daySubjectList.clear();
                        for (Thursday thursday : thursdays) {
                            daySubjectList.add(subjectViewModel.getSubject(thursday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "friday":
                    dayViewModel.getFridayList().observe(getViewLifecycleOwner(), fridays -> {
                        daySubjectList.clear();
                        for (Friday friday : fridays) {
                            daySubjectList.add(subjectViewModel.getSubject(friday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "saturday":
                    dayViewModel.getSaturdayList().observe(getViewLifecycleOwner(), saturdays -> {
                        daySubjectList.clear();
                        for (Saturday saturday : saturdays) {
                            daySubjectList.add(subjectViewModel.getSubject(saturday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                case "sunday":
                    dayViewModel.getSundayList().observe(getViewLifecycleOwner(), sundays -> {
                        daySubjectList.clear();
                        for (Sunday sunday : sundays) {
                            daySubjectList.add(subjectViewModel.getSubject(sunday.getSubjectName()));
                        }
                        timeTableAdapter.submitList(daySubjectList);
                    });
                    break;
                default:
                    break;
            }
        }
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