package com.attendancemanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class HomeFragment extends Fragment {

    private TextView mDate;
    private TextView mDay;
    private TextView mGreet;
    private TextView mProgressPercentage;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private DBHelper dbHelper;

    private List<Subject> mTodaySubjectList;
    private SubjectListAdapter mSubjectListAdapter;

    private static final String TAG = "HomeFragment";

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

        mDate = view.findViewById(R.id.date_text_view);
        mDay = view.findViewById(R.id.day_text_view);
        mGreet = view.findViewById(R.id.greet_text_view);
        mProgressBar = view.findViewById(R.id.overall_attendance_percentage);
        mProgressPercentage = view.findViewById(R.id.progressbar_percentage);
        mRecyclerView = view.findViewById(R.id.subject_recycler_view);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDayAndDate();
        getTodayTimeTable();
        buildRecyclerView();

    }

    private void setDayAndDate() {

        Calendar calendar = Calendar.getInstance();
        StringBuilder date = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        date.append(simpleDateFormat.format(calendar.getTime()).toString());
        date.append("th ");
        simpleDateFormat = new SimpleDateFormat("MMMM");
        date.append(simpleDateFormat.format(calendar.getTime()).toString());
        mDate.setText(date.toString());

        simpleDateFormat.applyPattern("EEEE");
        mDay.setText(simpleDateFormat.format(calendar.getTime()).toString());
        mGreet.setText("Hey there, Krithic");

        mProgressBar.setProgress(60);
        mProgressPercentage.setText("60%");
    }

    private void getTodayTimeTable() {

        dbHelper = new DBHelper(getContext());
//        mTodaySubjectList = new ArrayList<>();
//        dbHelper.addSubject(new Subject("English", 3, 2));
//        dbHelper.addSubject(new Subject("Maths", 3, 2));
//        dbHelper.addSubject(new Subject("Indian Constitution", 3, 2));
//        dbHelper.addSubject(new Subject("Physics", 3, 2));
//        dbHelper.addSubject(new Subject("Chemistry", 3, 2));
//        dbHelper.addSubject(new Subject("Computer Science", 3, 2));
//        dbHelper.addSubject(new Subject("Environmental Valued Science HELLO THIS IS BYE", 3, 2));
        mTodaySubjectList = dbHelper.getAllSubjects();
    }

    private void buildRecyclerView() {

        mSubjectListAdapter = new SubjectListAdapter(mTodaySubjectList, getContext());
        mSubjectListAdapter.setItemClickListener(new SubjectListAdapter.OnItemClickListener() {
            @Override
            public void onAttendButtonClick() {
                Toast.makeText(getContext(), "Attended Button Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBunkButtonClick() {
                Toast.makeText(getContext(), "Bunked Button Clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelledButtonClick() {
                Toast.makeText(getContext(), "Cancelled Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mSubjectListAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            ExpandableBottomBar bottomNavBar = getActivity().findViewById(R.id.bottom_bar);

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                    hideBottomNavigationView(bottomNavBar);
                else
                    showBottomNavigationView(bottomNavBar);
            }
        });

    }

    private void hideBottomNavigationView(ExpandableBottomBar view) {
        view.clearAnimation();
        view.animate().translationY(view.getHeight() + 24).setDuration(200).start();
    }

    public void showBottomNavigationView(ExpandableBottomBar view) {
        view.clearAnimation();
        view.animate().translationY(0).setDuration(200).start();
    }
}