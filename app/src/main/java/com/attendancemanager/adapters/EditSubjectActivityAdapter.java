package com.attendancemanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;

import java.util.Locale;

public class EditSubjectActivityAdapter extends ListAdapter<Subject, EditSubjectActivityAdapter.EditSubjectListViewHolder> {

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getAttendedClasses() == newItem.getAttendedClasses() &&
                    oldItem.getTotalClasses() == newItem.getTotalClasses() &&
                    oldItem.getSubjectName().equals(newItem.getSubjectName());
        }
    };
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public EditSubjectActivityAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    public void setItemClickListener(EditSubjectActivityAdapter.ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public EditSubjectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.edit_subject_list_item, parent, false);
        return new EditSubjectListViewHolder(view, mItemClickListener);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull EditSubjectActivityAdapter.EditSubjectListViewHolder holder, int position) {

        Subject subject = getItem(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round(((float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mProgressBar.setProgress(percentage);
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendedClasses.setText(String.format(mContext.getResources().getString(R.string.attended_info_template), subject.getAttendedClasses(), subject.getTotalClasses()));
        holder.mProgressPercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    public Subject getSubjectAt(int position) {
        return getItem(position);
    }

    public interface ItemClickListener {
        void OnItemClickListener(int position);
    }

    public static class EditSubjectListViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubjectName, mProgressPercentage, mAttendedClasses;
        private ProgressBar mProgressBar;
        private EditSubjectActivityAdapter.ItemClickListener mListener;

        public EditSubjectListViewHolder(@NonNull View itemView, EditSubjectActivityAdapter.ItemClickListener mListener) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.edit_subject_name);
            mProgressPercentage = itemView.findViewById(R.id.edit_subject_progressbar_percentage);
            mAttendedClasses = itemView.findViewById(R.id.edit_total_classes_attended);
            mProgressBar = itemView.findViewById(R.id.edit_subject_attendance_progressbar);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION)
                    mListener.OnItemClickListener(getAdapterPosition());
            });
        }
    }
}
