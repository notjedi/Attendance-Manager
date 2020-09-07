package com.attendancemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.SubjectListViewHolder> {

    private ArrayList<Subject> mTodaySubjectList;
    private OnItemClickListener mItemClickListener;
    private Context mContext;
    private static final String TAG = "SubjectListAdapter";

    public SubjectListAdapter(ArrayList<Subject> mTodaySubjectList, Context mContext) {
        this.mTodaySubjectList = mTodaySubjectList;
        this.mContext = mContext;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onAttendButtonClick();
        void onBunkButtonClick();
        void onCancelledButtonClick();
    }

    public static class SubjectListViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubjectName;
        private TextView mTotalClassesAttended;
        private TextView mStatusInfo;
        private TextView mAttendanceStatus;

        private ProgressBar mSubjectAttendanceProgressBar;
        private TextView mSubjectProgressBarPercentage;

        private ImageButton mAttended;
        private ImageButton mBunked;
        private ImageButton mCancelled;

        public SubjectListViewHolder(@NonNull final View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.subject_name);
            mTotalClassesAttended = itemView.findViewById(R.id.total_classes_attended);
            mStatusInfo = itemView.findViewById(R.id.status_info);
            mAttendanceStatus = itemView.findViewById(R.id.attendance_status);

            mSubjectAttendanceProgressBar = itemView.findViewById(R.id.subject_attendance_progressbar);
            mSubjectProgressBarPercentage = itemView.findViewById(R.id.subject_progressbar_percentage);

            mAttended = itemView.findViewById(R.id.attended_button);
            mBunked = itemView.findViewById(R.id.bunked_button);
            mCancelled = itemView.findViewById(R.id.cancelled_button);

            mAttended.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onAttendButtonClick();
                }
            });
            mBunked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onBunkButtonClick();
                }
            });
            mCancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onCancelledButtonClick();
                }
            });
        }

    }

    @NonNull
    @Override
    public SubjectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.subject_list_row, parent, false);
        return new SubjectListViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectListViewHolder holder, int position) {

        Subject subject = mTodaySubjectList.get(position);
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mTotalClassesAttended.setText(String.format(mContext.getResources().getString(R.string.attended_info_template), Integer.toString(subject.getAttendedClasses()), Integer.toString(subject.getTotalClasses())));
        holder.mSubjectAttendanceProgressBar.setProgress(30);
        holder.mSubjectProgressBarPercentage.setText("30%");

    }

    @Override
    public int getItemCount() {
        return mTodaySubjectList.size();
    }

}
