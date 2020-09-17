package com.attendancemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
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
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private DBHelper dbHelper;

    private String day;
    private List<Subject> mTodaySubjectList;
    private SubjectListAdapter mSubjectListAdapter;
    private SharedPreferences defaultPrefs;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDate = view.findViewById(R.id.date_text_view);
        mDay = view.findViewById(R.id.day_text_view);
        mGreet = view.findViewById(R.id.greet_text_view);
        mProgressBar = view.findViewById(R.id.overall_attendance_percentage);
        mProgressPercentage = view.findViewById(R.id.progressbar_percentage);
        mRecyclerView = view.findViewById(R.id.today_subject_recycler_view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDayAndDate();
        setProgressBar();
        getTodayTimeTable();
        buildRecyclerView();

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
        mDay.setText(simpleDateFormat.format(calendar.getTime()));
        String name = defaultPrefs.getString(SettingsFragment.NAME, null);
        mGreet.setText(String.format(Locale.getDefault(), "Hey there, %s", name));
    }

    private void setProgressBar() {

        mProgressBar.setProgress(60);
        mProgressPercentage.setText("60%");
    }

    private void getTodayTimeTable() {

        dbHelper = new DBHelper(getContext());

//        dbHelper.addSubject(new Subject("English", 10, 20));
//        dbHelper.addSubject(new Subject("Maths"));
//        dbHelper.addSubject(new Subject("Indian Constitution", 15, 20));
//        dbHelper.addSubject(new Subject("Physics", 5, 20));
//        dbHelper.addSubject(new Subject("Chemistry", 18, 20));
//        dbHelper.addSubject(new Subject("Computer Science", 8, 20));
//        dbHelper.addSubject(new Subject("Environmental Valued Science HELLO THIS IS BYE", 2, 3));
//        String[] names = new String[]{"English", "Maths", "Physics"};
//        dbHelper.insertSubjectToDayTable("monday", names);

        day = new SimpleDateFormat("EEEE", Locale.US).format(Calendar.getInstance().getTime()).toLowerCase();
        mTodaySubjectList = dbHelper.getSubjectsOfDay("monday");
    }

    private void buildRecyclerView() {

        mSubjectListAdapter = new SubjectListAdapter(mTodaySubjectList, getContext());
        mSubjectListAdapter.setItemClickListener(new SubjectListAdapter.OnItemClickListener() {
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
        mRecyclerView.setAdapter(mSubjectListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            ExpandableBottomBar bottomNavBar = getActivity().findViewById(R.id.bottom_bar);

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

    private void vibrateOnTouch(boolean vibrate) {

        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(70);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (EditSubjectActivity.changed) {
            mTodaySubjectList.clear();
            mTodaySubjectList.addAll(dbHelper.getSubjectsOfDay("monday"));
            mSubjectListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        /* Close the connection to the database although this method is not guaranteed to be called */
        /* https://stackoverflow.com/questions/17195641/fragment-lifecycle-when-ondestroy-and-ondestroyview-are-not-called */

        super.onDestroy();
        dbHelper.close();
    }
}