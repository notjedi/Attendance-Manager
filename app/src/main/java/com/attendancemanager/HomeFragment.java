package com.attendancemanager;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.behavior.ExpandableBottomBarScrollableBehavior;

public class HomeFragment extends Fragment {

    private TextView mDate;
    private TextView mDay;
    private TextView mGreet;
    private TextView mProgressPercentage;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;

    private ArrayList<Subject> mTodaySubjectList;
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
        mCoordinatorLayout = view.findViewById(R.id.nested_coordinator_layout);
        AppBarLayout appBarLayout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeDayAndDate();
        buildRecyclerView();

    }

    private void initializeDayAndDate() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        mDate.setText(simpleDateFormat.format(calendar.getTime()).toString());

        simpleDateFormat.applyPattern("EEEE");
        mDay.setText(simpleDateFormat.format(calendar.getTime()).toString());
        mGreet.setText("Hey there, Krithic");

        mProgressBar.setProgress(60);
        mProgressPercentage.setText("60%");
    }

    private void buildRecyclerView() {

        mTodaySubjectList = new ArrayList<>();
        mTodaySubjectList.add(new Subject("English", 3, 2));
        mTodaySubjectList.add(new Subject("Math", 3, 2));
        mTodaySubjectList.add(new Subject("EVS", 3, 2));
        mTodaySubjectList.add(new Subject("Chemistry", 3, 2));
        mTodaySubjectList.add(new Subject("Physics", 3, 2));
        mTodaySubjectList.add(new Subject("Computer Science", 3, 2));
        mTodaySubjectList.add(new Subject("Indian Constitution", 3, 2));


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
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mRecyclerView.getLayoutParams();
        int bottomMargin = lp.bottomMargin;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int totalDyPos = 0;
            private int totalDyNeg = 0;
            private boolean flagPos = false;
            private boolean flagNeg = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    totalDyNeg = 0;
                    flagNeg = false;

                    totalDyPos += dy;
                    if (totalDyPos > 100) {
                        if (!flagPos) {
                            totalDyPos = 0;
                            flagPos = true;
                        }
                        Log.i(TAG, "onScrolled: " + totalDyPos);
                        hideBottomNavigationView(getActivity().findViewById(R.id.bottom_bar), totalDyPos, bottomMargin);
                    }
                } else {
                    totalDyPos = 0;
                    flagPos = false;

                    totalDyNeg += dy;
                    Log.i(TAG, "onScrolled: " + totalDyNeg);
                    if (Math.abs(totalDyNeg) > 100) {
                        if (!flagNeg) {
                            totalDyNeg = 0;
                            flagNeg = true;
                        }
                        showBottomNavigationView(getActivity().findViewById(R.id.bottom_bar), Math.abs(totalDyNeg), bottomMargin);
                    }
                }
            }
        });

    }

    private void hideBottomNavigationView(ExpandableBottomBar view, int dy, int bottomMargin) {
        if (dy > view.getMeasuredHeight()) {
            dy = view.getMeasuredHeight() + 16 + 8;
        }
        view.clearAnimation();
        Log.i(TAG, "hideBottomNavigationView: inside hide " + dy);
        view.animate().translationY(dy).setDuration(300).start();
    }

    public void showBottomNavigationView(ExpandableBottomBar view, int dy, int bottomMargin) {
//        Log.i(TAG, "showBottomNavigationView: " + view.getTranslationY());

        Log.i(TAG, "showBottomNavigationView: inside show1 " + dy);
        dy = view.getMeasuredHeight() - dy;
        if (dy < 0)
            dy = 0;
        Log.i(TAG, "showBottomNavigationView: inside show2 " + dy);
        view.clearAnimation();
        view.animate().translationY(dy).setDuration(300).start();

//        final ValueAnimator animator = ValueAnimator.ofFloat(view.getTranslationY(), 0);
//        animator.setInterpolator(new DecelerateInterpolator());
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float animatedValue = (Float) animator.getAnimatedValue();
//                Log.i(TAG, "onAnimationUpdate: " + animatedValue);
//                if (animatedValue)
//                view.setTranslationY(animatedValue);
//            }
//        });
//        animator.start();
//        view.clearAnimation();
//        view.animate().translationY(0).setDuration(300);
    }
}