package com.attendancemanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;
import com.attendancemanager.viewmodel.DayViewModel;

import java.util.Locale;

public class HomeFragmentListAdapter extends ListAdapter<Subject, HomeFragmentListAdapter.SubjectListViewHolder> {

    private static final float VISIBLE_ALPHA = 1.0f;
    private static final float ATTENDED_ALPHA = 0.4f;
    private static final float BUNKED_ALPHA = 0.3f;
    private static final float CANCELLED_ALPHA = 0.5f;
    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        /* Using RecyclerView.ListAdapter and DiffUtil to pass on data changes from LiveData to RecyclerView */

        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getSubjectName().equals(newItem.getSubjectName()) &&
                    oldItem.getAttendedClasses() == newItem.getAttendedClasses() &&
                    oldItem.getTotalClasses() == newItem.getTotalClasses() &&
                    oldItem.getStatus() == newItem.getStatus();
        }
    };
    private final Context mContext;
    private OnItemClickListener mItemClickListener;
    private int criteria;

    public HomeFragmentListAdapter(Context mContext) {
        super(DIFF_CALLBACK);
        this.mContext = mContext;
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setAttendanceCriteria(int criteria) {
        this.criteria = criteria;
    }

    @NonNull
    @Override
    public SubjectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_subject_list, parent, false);
        return new SubjectListViewHolder(view, mItemClickListener);
    }

    public int getCriteria() {
        return criteria;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull SubjectListViewHolder holder, int position) {

        Subject subject = getItem(position);
        int due;
        int attended = subject.getAttendedClasses();
        int total = subject.getTotalClasses();
        int percentage = total == 0 ? 0 : Math.round((
                (float) attended / (float) total) * 100);

        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mTotalClassesAttended.setText(String.format(mContext.getResources().getString(R.string.attended_info_template),
                subject.getAttendedClasses(), subject.getTotalClasses()));
        holder.mSubjectProgressBarPercentage.setText(String.format(Locale.US, "%d%%", percentage));

        if (percentage < criteria) {
            /* Should have to attend more classes */
            holder.mSubjectAttendanceProgressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar_red));
            due = ((criteria * total) - (attended * 100)) / (100 - criteria);
            holder.mStatusInfo.setText(String.format(Locale.getDefault(), "Attend %d more classes", due));
        } else {
            /* On track or can bunk a few classes */
            holder.mSubjectAttendanceProgressBar.setProgressDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress_bar_green));
            if (percentage != criteria) {
                due = ((attended * 100) - (total * criteria)) / criteria;
                holder.mStatusInfo.setText(String.format(Locale.getDefault(), "Can bunk %d more classes", due));
            } else
                holder.mStatusInfo.setText(R.string.on_track_text);
        }
        /* A weird bug causes the progress to be 0 percent while updating the percentage. But
        setting the progress to 0 and then updating the progress again works, idk how, smh */
        holder.mSubjectAttendanceProgressBar.setProgress(0);
        holder.mSubjectAttendanceProgressBar.setProgress(percentage);

        switch (subject.getStatus()) {
            case DayViewModel.NONE:
                setAlpha(holder, VISIBLE_ALPHA, VISIBLE_ALPHA, VISIBLE_ALPHA);
                break;
            case DayViewModel.CANCELLED:
                setAlpha(holder, ATTENDED_ALPHA, BUNKED_ALPHA, VISIBLE_ALPHA);
                break;
            case DayViewModel.BUNKED:
                setAlpha(holder, ATTENDED_ALPHA, VISIBLE_ALPHA, CANCELLED_ALPHA);
                break;
            case DayViewModel.ATTENDED:
                setAlpha(holder, VISIBLE_ALPHA, BUNKED_ALPHA, CANCELLED_ALPHA);
                break;
        }
    }

    private void setAlpha(SubjectListViewHolder holder, float attendedAlpha, float bunkedAlpha, float cancelledAlpha) {
        holder.mAttended.setAlpha(attendedAlpha);
        holder.mBunked.setAlpha(bunkedAlpha);
        holder.mCancelled.setAlpha(cancelledAlpha);
    }

    public Subject getSubjectAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void onAttendButtonClick(int position);

        void onBunkButtonClick(int position);

        void onCancelledButtonClick(int position);
    }

    public static class SubjectListViewHolder extends RecyclerView.ViewHolder {

        private final TextView mSubjectName;
        private final TextView mStatusInfo;
        private final ProgressBar mSubjectAttendanceProgressBar;
        private final TextView mSubjectProgressBarPercentage;
        private final ImageButton mAttended;
        private final ImageButton mBunked;
        private final ImageButton mCancelled;
        public TextView mTotalClassesAttended;

        public SubjectListViewHolder(@NonNull final View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.subject_name);
            mTotalClassesAttended = itemView.findViewById(R.id.total_classes_attended);
            mStatusInfo = itemView.findViewById(R.id.status_info);

            mSubjectAttendanceProgressBar = itemView.findViewById(R.id.subject_attendance_progressbar);
            mSubjectProgressBarPercentage = itemView.findViewById(R.id.subject_progressbar_percentage);

            mAttended = itemView.findViewById(R.id.attended_button);
            mBunked = itemView.findViewById(R.id.bunked_button);
            mCancelled = itemView.findViewById(R.id.cancelled_button);

            mAttended.setOnClickListener(view -> itemClickListener.onAttendButtonClick(getAdapterPosition()));
            mBunked.setOnClickListener(view -> itemClickListener.onBunkButtonClick(getAdapterPosition()));
            mCancelled.setOnClickListener(view -> itemClickListener.onCancelledButtonClick(getAdapterPosition()));
        }

    }

}
