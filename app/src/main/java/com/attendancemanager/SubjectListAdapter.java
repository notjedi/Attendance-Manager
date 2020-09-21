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

import java.util.List;
import java.util.Locale;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.SubjectListViewHolder> {

    private static final String TAG = "SubjectListAdapter";
    private List<Subject> mTodaySubjectList;
    private OnItemClickListener mItemClickListener;
    private Context mContext;
    private boolean mVibrate;

    public SubjectListAdapter(List<Subject> mTodaySubjectList, Context mContext) {
        this.mTodaySubjectList = mTodaySubjectList;
        this.mContext = mContext;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public SubjectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.subject_list_item, parent, false);
        return new SubjectListViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectListViewHolder holder, int position) {

//        TODO: set progress bar color based on attendance
//        TODO: set status

        Subject subject = mTodaySubjectList.get(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round(((float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mTotalClassesAttended.setText(String.format(mContext.getResources().getString(R.string.attended_info_template), subject.getAttendedClasses(), subject.getTotalClasses()));
        holder.mSubjectAttendanceProgressBar.setProgress(percentage);
        holder.mSubjectProgressBarPercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    @Override
    public int getItemCount() {
        return mTodaySubjectList.size();
    }

    public interface OnItemClickListener {
        void onAttendButtonClick();

        void onBunkButtonClick();

        void onCancelledButtonClick();
    }

    public static class SubjectListViewHolder extends RecyclerView.ViewHolder {

        private static final float attendedAlpha = 0.4f;
        private static final float bunkedAlpha = 0.3f;
        private static final float cancelledAlpha = 0.5f;
        private static final float visibleAlpha = 1.0f;
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

            mAttended.setOnClickListener(view -> {
                mAttended.setAlpha(visibleAlpha);
                mBunked.setAlpha(bunkedAlpha);
                mCancelled.setAlpha(cancelledAlpha);
                itemClickListener.onAttendButtonClick();
            });
            mBunked.setOnClickListener(view -> {
                mAttended.setAlpha(attendedAlpha);
                mBunked.setAlpha(visibleAlpha);
                mCancelled.setAlpha(cancelledAlpha);
                itemClickListener.onBunkButtonClick();
            });
            mCancelled.setOnClickListener(view -> {
                mAttended.setAlpha(attendedAlpha);
                mBunked.setAlpha(bunkedAlpha);
                mCancelled.setAlpha(visibleAlpha);
                itemClickListener.onCancelledButtonClick();
            });
        }

    }

}
