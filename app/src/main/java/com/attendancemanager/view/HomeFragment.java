package com.attendancemanager.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.model.Friday;
import com.attendancemanager.model.Saturday;
import com.attendancemanager.model.Sunday;
import com.attendancemanager.model.Thursday;
import com.attendancemanager.model.Tuesday;
import com.attendancemanager.model.Wednesday;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.R;
import com.attendancemanager.viewmodel.SubjectViewModel;
import com.attendancemanager.adapters.BottomSheetAdapter;
import com.attendancemanager.adapters.HomeFragmentListAdapter;
import com.attendancemanager.model.Monday;
import com.attendancemanager.model.Subject;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView mDate;
    private TextView mDay;
    private TextView mGreet;
    private TextView mProgressPercentage;
    private RecyclerView mRecyclerView;
    private RecyclerView mBottomSheetRecyclerView;
    private ImageButton mExtraClassButton;
    private ProgressBar mProgressBar;
    private ConstraintLayout mBottomSheetLayout;
    private ExpandableBottomBar bottomNavBar;

    private String day;
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private SharedPreferences defaultPrefs;
    private List<Subject> mAllSubjectList;
    private List<Subject> mTodaySubjectList;
    private HomeFragmentListAdapter homeFragmentListAdapter;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior mBottomSheetBehavior;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDate = view.findViewById(R.id.date_text_view);
        mDay = view.findViewById(R.id.day_text_view);
        mGreet = view.findViewById(R.id.greet_text_view);
        mProgressBar = view.findViewById(R.id.overall_attendance_percentage);
        mProgressPercentage = view.findViewById(R.id.progressbar_percentage);
        mRecyclerView = view.findViewById(R.id.today_subject_recycler_view);
        mExtraClassButton = view.findViewById(R.id.extra_class_button);
        mBottomSheetLayout = view.findViewById(R.id.bottom_sheet_constraint_layout);
        mBottomSheetRecyclerView = view.findViewById(R.id.bottom_sheet_recycler_view);
        bottomNavBar = getActivity().findViewById(R.id.bottom_bar);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAllSubjectList = new ArrayList<>();
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> {
            mAllSubjectList.clear();
            mAllSubjectList.addAll(subjects);
        });

        setDayAndDate();
        setProgressBar();
        getTodayTimeTable();
        buildRecyclerView();
        buildBottomSheetRecyclerView();
    }

    private void setDayAndDate() {

        Calendar calendar = Calendar.getInstance();
        StringBuilder date = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.US);
        date.append(simpleDateFormat.format(calendar.getTime()));
        date.append("th ");
        simpleDateFormat.applyPattern("MMMM");
        date.append(simpleDateFormat.format(calendar.getTime()));
        mDate.setText(date.toString());

        simpleDateFormat.applyPattern("EEEE");
        day = simpleDateFormat.format(calendar.getTime()).toLowerCase();
        mDay.setText(simpleDateFormat.format(calendar.getTime()));
        String name = defaultPrefs.getString(SettingsFragment.NAME, null);
        mGreet.setText(String.format(Locale.getDefault(), "Hey there, %s", name));
    }

    private void setProgressBar() {

        mProgressBar.setProgress(60);
        mProgressPercentage.setText("60%");
    }

    private void getTodayTimeTable() {
        mTodaySubjectList = new ArrayList<>();
        switch (day) {
            case "Monday":
                dayViewModel.getMondayList().observe(getViewLifecycleOwner(), mondays -> {
                    mTodaySubjectList.clear();
                    for (Monday monday: mondays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(monday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Tuesday":
                dayViewModel.getTuesdayList().observe(getViewLifecycleOwner(), tuesdays -> {
                    mTodaySubjectList.clear();
                    for (Tuesday tuesday: tuesdays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(tuesday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Wednesday":
                dayViewModel.getWednesdayList().observe(getViewLifecycleOwner(), wednesdays -> {
                    mTodaySubjectList.clear();
                    for (Wednesday wednesday: wednesdays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(wednesday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Thursday":
                dayViewModel.getThursdayList().observe(getViewLifecycleOwner(), thursdays -> {
                    mTodaySubjectList.clear();
                    for (Thursday thursday: thursdays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(thursday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Friday":
                dayViewModel.getFridayList().observe(getViewLifecycleOwner(), fridays -> {
                    mTodaySubjectList.clear();
                    for (Friday friday: fridays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(friday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Saturday":
                dayViewModel.getSaturdayList().observe(getViewLifecycleOwner(), saturdays -> {
                    mTodaySubjectList.clear();
                    for (Saturday saturday: saturdays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(saturday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            case "Sunday":
                dayViewModel.getSundayList().observe(getViewLifecycleOwner(), sundays -> {
                    mTodaySubjectList.clear();
                    for (Sunday sunday: sundays) {
                        mTodaySubjectList.add(subjectViewModel.getSubject(sunday.getSubjectName()));
                    }
                    homeFragmentListAdapter.submitList(mTodaySubjectList);
                });
                break;
            default:
                break;
        }
    }

    private void buildRecyclerView() {

        homeFragmentListAdapter = new HomeFragmentListAdapter(getContext());
        homeFragmentListAdapter.setItemClickListener(new HomeFragmentListAdapter.OnItemClickListener() {
            boolean vibrate = defaultPrefs.getBoolean(SettingsFragment.VIBRATE, true);

            @Override
            public void onAttendButtonClick() {
                vibrateOnTouch(vibrate);
                Toast.makeText(getContext(), "Attended Button Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBunkButtonClick() {
                vibrateOnTouch(vibrate);
                Toast.makeText(getContext(), "Bunked Button Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelledButtonClick() {
                vibrateOnTouch(vibrate);
                Toast.makeText(getContext(), "Cancelled Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(homeFragmentListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    /* Scrolled down */
                    bottomNavBar.hide();
                } else {
                    /* Scrolled up */
                    bottomNavBar.show();
                }
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildBottomSheetRecyclerView() {

        mExtraClassButton.setOnClickListener(v -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED));

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomNavBar.setVisibility(View.VISIBLE);
                } else {
                    bottomNavBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        BottomSheetAdapter mBottomSheetAdapter = new BottomSheetAdapter(mAllSubjectList);
        mBottomSheetAdapter.setOnAddButtonClickListener(position -> {
            mTodaySubjectList.add(mAllSubjectList.get(position));
            homeFragmentListAdapter.notifyItemInserted(mTodaySubjectList.size() - 1);
        });

        mBottomSheetRecyclerView.setAdapter(mBottomSheetAdapter);
        mBottomSheetRecyclerView.setHasFixedSize(true);
        mBottomSheetRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBottomSheetRecyclerView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });
        mBottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void vibrateOnTouch(boolean vibrate) {

        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.EFFECT_TICK));
                } else {
                    vibrator.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                //deprecated in API 26
                vibrator.vibrate(70);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        /* Close the connection to the database although this method is not guaranteed to be called */
        /* https://stackoverflow.com/questions/17195641/fragment-lifecycle-when-ondestroy-and-ondestroyview-are-not-called */

        super.onDestroy();
        subjectViewModel.closeDB();
    }
}