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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.adapters.BottomSheetAdapter;
import com.attendancemanager.adapters.HomeFragmentListAdapter;
import com.attendancemanager.model.Subject;
import com.attendancemanager.model.SubjectMinimal;
import com.attendancemanager.viewmodel.DayViewModel;
import com.attendancemanager.viewmodel.SubjectViewModel;
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
    private LinearLayout mBottomSheetLayout;
    private ExpandableBottomBar bottomNavBar;

    private String day;
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private SharedPreferences defaultPrefs;
    private List<Subject> mAllSubjectList;
    private List<Subject> mTodaySubjectList;
    private BottomSheetAdapter mBottomSheetAdapter;
    private HomeFragmentListAdapter homeFragmentListAdapter;
    @SuppressWarnings("rawtypes")
    private BottomSheetBehavior mBottomSheetBehavior;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBottomSheetAdapter = new BottomSheetAdapter();
        mAllSubjectList = new ArrayList<>();
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        dayViewModel = new ViewModelProvider(this).get(DayViewModel.class);
        subjectViewModel = new ViewModelProvider(this).get(SubjectViewModel.class);
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

        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> {
            mAllSubjectList.clear();
            mAllSubjectList.addAll(subjects);
            mBottomSheetAdapter.submitList(subjects);
        });

        setDayAndDate();
        setProgressBar();
        getTodayTimeTable();
        buildRecyclerView();
        buildBottomSheetRecyclerView();

    }

    private void setDayAndDate() {
        /* Sets the data and greet text */

        Calendar calendar = Calendar.getInstance();
        StringBuilder date = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.US);
        date.append(simpleDateFormat.format(calendar.getTime()));
        date.append("th ");
        simpleDateFormat.applyPattern("MMMM");
        date.append(simpleDateFormat.format(calendar.getTime()));
        mDate.setText(date.toString());

        simpleDateFormat.applyPattern("EEEE");
        day = simpleDateFormat.format(calendar.getTime());
        mDay.setText(simpleDateFormat.format(calendar.getTime()));
        String name = defaultPrefs.getString(SettingsFragment.NAME, null);
        mGreet.setText(String.format(Locale.getDefault(), "Hey there, %s", name));
    }

    private void setProgressBar() {
        /* Sets main progress bar progress */

        mProgressBar.setProgress(60);
        mProgressPercentage.setText("60%");
    }

    private void getTodayTimeTable() {
        /* Gets all the subjects for the current day for corresponding table */

        mTodaySubjectList = new ArrayList<>();
        switch (day.toLowerCase()) {
            case "monday":
                dayViewModel.getMondayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "tuesday":
                dayViewModel.getTuesdayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "wednesday":
                dayViewModel.getWednesdayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "thursday":
                dayViewModel.getThursdayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "friday":
                dayViewModel.getFridayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "saturday":
                dayViewModel.getSaturdayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            case "sunday":
                dayViewModel.getSundayList().observe(getViewLifecycleOwner(), subjectMinimalList -> {
                    List<Subject> subjectList = new ArrayList<>();
                    mTodaySubjectList.clear();
                    for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                        Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                        if (subject != null) {
                            mTodaySubjectList.add(subject);
                            subjectList.add(subject);
                        }
                    }
                    homeFragmentListAdapter.submitList(subjectList);
                });
                break;
            default:
                break;
        }
    }

    private void notifyChanges(SubjectMinimal subjectMinimal) {

    }

    private void buildRecyclerView() {
        /* Builds the recycler view */

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
        /* Builds the bottom sheet to add extra subjects for day */

        mExtraClassButton.setOnClickListener(v -> {
            /* Bottom sheet kinda has a weird bug which triggers newState change without clicking on
            the mExtraClassButton (currently observed only when the recycler view is empty), so
            setting the visibility to View.GONE in the XML file and making it VISIBLE onClicking
            the mExtraClassButton. This somehow gets rid of the bug. */
            /* TODO animate opening the sheet for the first time */
            mBottomSheetLayout.setVisibility(View.VISIBLE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomNavBar.setVisibility(View.GONE);
        });

        /* TODO do not display bottom bar if list of subjects is empty */
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        mBottomSheetBehavior.setDraggable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomNavBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mBottomSheetAdapter.setOnAddButtonClickListener(position -> {
            /* TODO: add subject to cache table */
            mTodaySubjectList.add(mAllSubjectList.get(position));
            homeFragmentListAdapter.notifyItemInserted(mTodaySubjectList.size() - 1);
        });

        mBottomSheetRecyclerView.setAdapter(mBottomSheetAdapter);
        mBottomSheetRecyclerView.setHasFixedSize(true);
        mBottomSheetRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBottomSheetRecyclerView.setOnTouchListener((v, event) -> {
            /* Prevent bottom bar from popping up while interacting with the bottom bar */
            v.getParent().requestDisallowInterceptTouchEvent(true);
            v.onTouchEvent(event);
            return true;
        });
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
    public void onDestroy() {
        /* Close the connection to the database although this method is not guaranteed to be called */
        /* https://stackoverflow.com/questions/17195641/fragment-lifecycle-when-ondestroy-and-ondestroyview-are-not-called */

        super.onDestroy();
    }
}