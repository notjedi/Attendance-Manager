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
    private String todayDate;
    private boolean vibrate;
    private SubjectViewModel subjectViewModel;
    private DayViewModel dayViewModel;
    private SharedPreferences defaultPrefs;
    private SharedPreferences sharedPrefs;
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
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPrefs = getContext().getSharedPreferences(MainActivity.SHARED_PREFS_SETTINGS_FILE_KEY, Context.MODE_PRIVATE);
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

        setDayAndDate();
        if (!sharedPrefs.getString(MainActivity.SHARED_PREFS_LAST_UPDATED, "notUpdated").equals(todayDate))
            dayViewModel.resetStatus(day);
        if (!sharedPrefs.getString(MainActivity.SHARED_PREFS_EXTRA_LAST_ADDED, "").equals(todayDate) &&
                !sharedPrefs.getString(MainActivity.SHARED_PREFS_EXTRA_LAST_ADDED, "").isEmpty()) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.SHARED_PREFS_SETTINGS_FILE_KEY, Context.MODE_PRIVATE);
            String dayAdded = sharedPreferences.getString(MainActivity.SHARED_PREFS_DAY_ADDED, "");
            int extraAdded = sharedPreferences.getInt(MainActivity.SHARED_PREFS_TOTAL_EXTRA_SUBJECTS_ADDED, 0);

            if (!dayAdded.isEmpty() && extraAdded != 0) {
                dayViewModel.deleteLimited(dayAdded, extraAdded);
                SharedPreferences.Editor sEditor = sharedPreferences.edit();
                sEditor.putString(MainActivity.SHARED_PREFS_EXTRA_LAST_ADDED, "");
                sEditor.putInt(MainActivity.SHARED_PREFS_TOTAL_EXTRA_SUBJECTS_ADDED, 0);
                sEditor.putString(MainActivity.SHARED_PREFS_DAY_ADDED, "");
                sEditor.apply();
            }
        }
        getTodayTimeTable();
        buildRecyclerView();
        buildBottomSheetRecyclerView();
    }

    private void setDayAndDate() {
        /* Sets the data and greet text */

        Calendar calendar = Calendar.getInstance();
        StringBuilder date = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d", Locale.US);
        String dateNumber = simpleDateFormat.format(calendar.getTime());
        date.append(dateNumber);
        switch (dateNumber) {
            case "1":
            case "21":
            case "31":
                date.append("st ");
                break;
            case "2":
            case "22":
                date.append("nd ");
                break;
            case "3":
            case "23":
                date.append("rd ");
                break;
            default:
                date.append("th ");
                break;
        }
        simpleDateFormat.applyPattern("MMMM");
        date.append(simpleDateFormat.format(calendar.getTime()));
        mDate.setText(date.toString());

        simpleDateFormat.applyPattern("EEEE");
        day = simpleDateFormat.format(calendar.getTime());
        mDay.setText(simpleDateFormat.format(calendar.getTime()));
        String name = defaultPrefs.getString(SettingsFragment.NAME, null);
        mGreet.setText(String.format(Locale.getDefault(), "Hey there, %s", name));
        simpleDateFormat.applyPattern("dMM");
        todayDate = simpleDateFormat.format(calendar.getTime());
        vibrate = defaultPrefs.getBoolean(SettingsFragment.VIBRATE, true);
    }

    private void getTodayTimeTable() {
        /* Gets all the subjects for the current day for corresponding table */


        subjectViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> {
            updateMainProgressBar(subjects);
            mBottomSheetAdapter.submitList(subjects);
            if (EditSubjectActivity.CHANGED != 1)
                return;
            List<Subject> subjectList = new ArrayList<>();
            for (SubjectMinimal subjectMinimal : dayViewModel.getSubjectList(day)) {
                for (Subject subject : subjects) {
                    if (subject.getSubjectName().equals(subjectMinimal.getSubjectName())) {
                        subject.setStatus(subjectMinimal.getStatus());
                        subjectList.add(subject);
                        break;
                    }
                }
            }
            homeFragmentListAdapter.submitList(subjectList);
            EditSubjectActivity.resetChanged();
        });

        dayViewModel.getDaySubjectList(day).observe(getViewLifecycleOwner(), subjectMinimalList -> {
            List<Subject> subjectList = new ArrayList<>();
            for (SubjectMinimal subjectMinimal : subjectMinimalList) {
                Subject subject = subjectViewModel.getSubject(subjectMinimal.getSubjectName());
                if (subject != null) {
                    subject.setStatus(subjectMinimal.getStatus());
                    subjectList.add(subject);
                }
            }
            /* https://stackoverflow.com/a/50031492 */
            homeFragmentListAdapter.submitList(subjectList);
        });
    }

    private void buildRecyclerView() {
        /* Builds the recycler view */

        homeFragmentListAdapter = new HomeFragmentListAdapter(getContext());
        homeFragmentListAdapter.setAttendanceCriteria(sharedPrefs.getInt(MainActivity.SHARED_PREFS_ATTENDANCE_CRITERIA, 75));
        homeFragmentListAdapter.setItemClickListener(new HomeFragmentListAdapter.OnItemClickListener() {

            @Override
            public void onAttendButtonClick(int position) {
                /* For some reason notifyItemChanged is not working as expected, it resets the button alpha on first click */
                Subject subject = homeFragmentListAdapter.getSubjectAt(position);
                SubjectMinimal subjectMinimal = dayViewModel.getSubjectList(day).get(position);
                if (subjectMinimal.getStatus() == DayViewModel.ATTENDED) {
                    subject.decrementTotalClasses();
                    subject.decrementAttendedClasses();
                    subjectMinimal.setStatus(DayViewModel.NONE);
                } else if (subjectMinimal.getStatus() == DayViewModel.BUNKED) {
                    subject.decrementTotalClasses();
                    subject.incrementTotalClasses();
                    subject.incrementAttendedClasses();
                    subjectMinimal.setStatus(DayViewModel.ATTENDED);
                } else {
                    subject.incrementTotalClasses();
                    subject.incrementAttendedClasses();
                    subjectMinimal.setStatus(DayViewModel.ATTENDED);
                }
                vibrateOnTouch(vibrate);
                subjectMinimal.setDay(day);
                subjectViewModel.update(subject);
                dayViewModel.update(subjectMinimal);
                homeFragmentListAdapter.notifyItemChanged(position);
                setLastUpdated();
            }

            @Override
            public void onBunkButtonClick(int position) {
                Subject subject = homeFragmentListAdapter.getSubjectAt(position);
                SubjectMinimal subjectMinimal = dayViewModel.getSubjectList(day).get(position);
                if (subjectMinimal.getStatus() == DayViewModel.BUNKED) {
                    subject.decrementTotalClasses();
                    subjectMinimal.setStatus(DayViewModel.NONE);
                } else if (subjectMinimal.getStatus() == DayViewModel.ATTENDED) {
                    subject.decrementTotalClasses();
                    subject.decrementAttendedClasses();
                    subject.incrementTotalClasses();
                    subjectMinimal.setStatus(DayViewModel.BUNKED);
                } else {
                    subject.incrementTotalClasses();
                    subjectMinimal.setStatus(DayViewModel.BUNKED);
                }
                vibrateOnTouch(vibrate);
                subjectMinimal.setDay(day);
                subjectViewModel.update(subject);
                dayViewModel.update(subjectMinimal);
                homeFragmentListAdapter.notifyItemChanged(position);
                setLastUpdated();
            }

            @Override
            public void onCancelledButtonClick(int position) {
                Subject subject = homeFragmentListAdapter.getSubjectAt(position);
                SubjectMinimal subjectMinimal = dayViewModel.getSubjectList(day).get(position);
                if (subjectMinimal.getStatus() == DayViewModel.ATTENDED) {
                    subject.decrementAttendedClasses();
                    subject.decrementTotalClasses();
                    subjectMinimal.setStatus(DayViewModel.CANCELLED);
                } else if (subjectMinimal.getStatus() == DayViewModel.BUNKED) {
                    subject.decrementTotalClasses();
                    subjectMinimal.setStatus(DayViewModel.CANCELLED);
                } else if (subjectMinimal.getStatus() == DayViewModel.NONE)
                    subjectMinimal.setStatus(DayViewModel.CANCELLED);
                else
                    subjectMinimal.setStatus(DayViewModel.NONE);
                vibrateOnTouch(vibrate);
                subjectMinimal.setDay(day);
                subjectViewModel.update(subject);
                dayViewModel.update(subjectMinimal);
                homeFragmentListAdapter.notifyItemChanged(position);
                setLastUpdated();
            }
        });
        mRecyclerView.setAdapter(homeFragmentListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        /* disable flash animation while notifyItemChanged() is called
        https://stackoverflow.com/a/36571561 */
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        // the below one works too
        //  ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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
            if (mBottomSheetAdapter.getItemCount() == 0) {
                Toast.makeText(getContext(), "Add a few subjects", Toast.LENGTH_SHORT).show();
                return;
            }
            mBottomSheetLayout.setVisibility(View.VISIBLE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomNavBar.setVisibility(View.GONE);
        });

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
            Subject subject = mBottomSheetAdapter.getSubjectAt(position);
            SubjectMinimal subjectMinimal = new SubjectMinimal(subject.getSubjectName(), day);
            subjectMinimal.setStatus(DayViewModel.NONE);
            dayViewModel.insert(subjectMinimal);
            int extraSubjectAdded = sharedPrefs.getInt(MainActivity.SHARED_PREFS_TOTAL_EXTRA_SUBJECTS_ADDED, 0);

            SharedPreferences.Editor sEditor = sharedPrefs.edit();
            sEditor.putString(MainActivity.SHARED_PREFS_EXTRA_LAST_ADDED, todayDate);
            sEditor.putString(MainActivity.SHARED_PREFS_DAY_ADDED, day);
            sEditor.putInt(MainActivity.SHARED_PREFS_TOTAL_EXTRA_SUBJECTS_ADDED, extraSubjectAdded + 1);
            sEditor.apply();
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

    private void updateMainProgressBar(List<Subject> subjectList) {
        int totalClasses = 0;
        int attendedClasses = 0;
        int attendancePercentage;
        for (Subject subject : subjectList) {
            totalClasses += subject.getTotalClasses();
            attendedClasses += subject.getAttendedClasses();
        }
        if (totalClasses != 0)
            attendancePercentage = Math.round(((float) attendedClasses / (float) totalClasses) * 100);
        else
            attendancePercentage = 0;
        mProgressBar.setProgress(attendancePercentage);
        mProgressPercentage.setText(String.format(Locale.US, "%d%%", attendancePercentage));
    }

    @SuppressWarnings("ConstantConditions")
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

    private void setLastUpdated() {
        SharedPreferences.Editor shEditor = sharedPrefs.edit();
        shEditor.putString(MainActivity.SHARED_PREFS_LAST_UPDATED, todayDate);
        shEditor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsFragment.DATA_CHANGED == 1) {
            String name = defaultPrefs.getString(SettingsFragment.NAME, null);
            vibrate = defaultPrefs.getBoolean(SettingsFragment.VIBRATE, true);
            mGreet.setText(String.format(Locale.getDefault(), "Hey there, %s", name));
            int criteria = sharedPrefs.getInt(MainActivity.SHARED_PREFS_ATTENDANCE_CRITERIA, 75);
            if (homeFragmentListAdapter.getCriteria() != criteria) {
                homeFragmentListAdapter.setAttendanceCriteria(criteria);
                homeFragmentListAdapter.notifyDataSetChanged();
            }
            SettingsFragment.resetDataChanged();
        }
    }

    @Override
    public void onDestroy() {
        /* Close the connection to the database although this method is not guaranteed to be called */
        /* https://stackoverflow.com/questions/17195641/fragment-lifecycle-when-ondestroy-and-ondestroyview-are-not-called */

        super.onDestroy();
    }
}