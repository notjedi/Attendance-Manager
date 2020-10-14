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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.attendancemanager.R;
import com.attendancemanager.adapters.BottomSheetAdapter;
import com.attendancemanager.adapters.TimeTableFragmentAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.TimeTableSubject;
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

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class TimeTableFragment extends Fragment {

    public static final String[] DAY_NAMES = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};
    public static MutableLiveData<Boolean> isEditable = new MutableLiveData<>(false);
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ExtendedFloatingActionButton addButtonFab;
    private FloatingActionButton floatingActionButton;
    private RecyclerView mBottomSheetRecyclerView;
    private LinearLayout mBottomSheetLayout;
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

        mBottomSheetAdapter = new BottomSheetAdapter();
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
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.day_view_pager);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        addButtonFab = view.findViewById(R.id.add_extended_fab);
        mBottomSheetLayout = view.findViewById(R.id.bottom_sheet_constraint_layout);
        mBottomSheetRecyclerView = view.findViewById(R.id.bottom_sheet_recycler_view);

        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> mBottomSheetAdapter.submitList(subjects));

        toolbar.setTitleTextAppearance(getContext(), R.style.RubixFontStyle);

        floatingActionButton.setOnClickListener(v -> {
            if (addButtonFab.getVisibility() == View.VISIBLE) {
                /* not Editable */
                addButtonFab.setVisibility(View.GONE);
                floatingActionButton.setImageResource(R.drawable.ic_edit);
                isEditable.setValue(false);
            } else {
                /* Editable */
                addButtonFab.setVisibility(View.VISIBLE);
                floatingActionButton.setImageResource(R.drawable.ic_check);
                isEditable.setValue(true);
            }
        });

        addButtonFab.setOnClickListener(v -> {
            if (mBottomSheetAdapter.getItemCount() == 0) {
                Toast.makeText(getContext(), "Add a few subjects", Toast.LENGTH_SHORT).show();
                return;
            }
            mBottomSheetLayout.setVisibility(View.VISIBLE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            addButtonFab.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
            getActivity().findViewById(R.id.bottom_bar).setVisibility(View.INVISIBLE);
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
        /* 0 represents SUNDAY in the Calender class so doing some calculations to make 0 as MONDAY */
        viewPager.setCurrentItem((calendar.get(Calendar.DAY_OF_WEEK) + 12) % 7);
        viewPager.setOffscreenPageLimit(1);
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
                    ExpandableBottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);
                    bottomBar.setVisibility(View.VISIBLE);
                    bottomBar.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mBottomSheetAdapter.setOnAddButtonClickListener(position -> {
            TimeTableSubject subject = new TimeTableSubject(
                    mBottomSheetAdapter.getSubjectAt(position).getSubjectName(),
                    DayViewModel.NONE,
                    pagerAdapter.getPageTitle(viewPager.getCurrentItem()).toString());
            dayViewModel.insert(subject);
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
        private TimeTableFragmentAdapter timeTableAdapter;
        private DayViewModel dayViewModel;
        private SubjectViewModel subjectViewModel;
        private String argDay;

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
            argDay = getArguments().getString(ARG_DAY_NAME);
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

            RecyclerView timeTableRecyclerView = view.findViewById(R.id.time_table_recycler_view);
            dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
            subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);

            timeTableAdapter = new TimeTableFragmentAdapter();
            timeTableRecyclerView.setAdapter(timeTableAdapter);
            timeTableRecyclerView.setHasFixedSize(true);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    TimeTableSubject timeTableSubject = dayViewModel.getSubjectsOfDay(argDay).get(position);
                    dayViewModel.delete(timeTableSubject);
                }
            });

            TimeTableFragment.isEditable.observe(getViewLifecycleOwner(), isEditable -> {
                if (isEditable)
                    itemTouchHelper.attachToRecyclerView(timeTableRecyclerView);
                else
                    itemTouchHelper.attachToRecyclerView(null);
            });

            dayViewModel.getSubjectsOfDayWithoutTemp(argDay).observe(getViewLifecycleOwner(), timeTableSubjectList -> {
                List<Subject> subjectList = new ArrayList<>();
                for (TimeTableSubject timeTableSubject : timeTableSubjectList) {
                    Subject subject = subjectViewModel.getSubject(timeTableSubject.getSubjectName());
                    if (subject != null) {
                        subjectList.add(subject);
                    }
                }
                timeTableAdapter.setSubjectList(subjectList);
            });
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