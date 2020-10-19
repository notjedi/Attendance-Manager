package com.attendancemanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;

import java.util.Locale;

public class PredictAdapter extends ListAdapter<Subject, PredictAdapter.PredictViewHolder> {

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            /* no need to check if attended classes are same cause it's always the same */
            return oldItem.getTotalClasses() == newItem.getTotalClasses();
        }
    };

    private final Context mContext;
    private int mCriteria;

    public PredictAdapter(Context mContext) {
        super(DIFF_CALLBACK);
        this.mContext = mContext;
    }

    public Subject getSubjectAt(int index) {
        return getItem(index);
    }

    public void setCriteria(int mCriteria) {
        this.mCriteria = mCriteria;
    }

    @NonNull
    @Override
    public PredictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_predict_list, parent, false);
        return new PredictViewHolder(view);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull PredictViewHolder holder, int position) {

        Subject subject = getItem(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round((
                (float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendedClasses.setText(String.format(mContext.getResources()
                .getString(R.string.attended_info_template), subject.getAttendedClasses(), subject.getTotalClasses()));
        holder.mProgressPercentage.setText(String.format(Locale.US, "%d%%", percentage));

        if (percentage < mCriteria)
            holder.mProgressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar_red));
        else
            holder.mProgressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar_green));
        holder.mProgressBar.setProgress(0);
        holder.mProgressBar.setProgress(percentage);
    }

    public static class PredictViewHolder extends RecyclerView.ViewHolder {

        private final TextView mSubjectName;
        private final TextView mProgressPercentage;
        private final TextView mAttendedClasses;
        private final ProgressBar mProgressBar;

        public PredictViewHolder(@NonNull View itemView) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.predict_subject_name);
            mProgressPercentage = itemView.findViewById(R.id.predict_subject_progressbar_percentage);
            mAttendedClasses = itemView.findViewById(R.id.predict_total_classes_attended);
            mProgressBar = itemView.findViewById(R.id.predict_subject_attendance_progressbar);
        }
    }
}