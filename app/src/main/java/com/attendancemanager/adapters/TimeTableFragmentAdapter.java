package com.attendancemanager.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;

import java.util.Locale;

public class TimeTableFragmentAdapter extends ListAdapter<Subject, TimeTableFragmentAdapter.TimeTableViewHolder> {

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        /* Using RecyclerView.ListAdapter and DiffUtil to pass on data changes from LiveData to RecyclerView */

        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            Log.i("CHECK ID", "areItemsTheSame: " + String.valueOf(oldItem.getId() == newItem.getId()));
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            /* No need to check if attended classes and total classes are same */
            /* TODO check if attendance is getting updated */
            Log.i("CHECK NAME", "areContentsTheSame: " + oldItem.getSubjectName().equals(newItem.getSubjectName()));
            return oldItem.getSubjectName().equals(newItem.getSubjectName());
        }
    };

    public TimeTableFragmentAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_table_item, parent, false);
        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {

        Subject subject = getItem(position);
        Log.i("TAH", "onBindViewHolder: " + getItemCount() + " " + subject.getId());
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round((
                (float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mPosition.setText(String.valueOf(position + 1));
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendancePercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    public Subject getSubjectAt(int position) {
        return getItem(position);
    }

    public static class TimeTableViewHolder extends RecyclerView.ViewHolder {

        private TextView mPosition;
        private TextView mSubjectName;
        private TextView mAttendancePercentage;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);

            mPosition = itemView.findViewById(R.id.subject_position);
            mSubjectName = itemView.findViewById(R.id.time_table_subject_name);
            mAttendancePercentage = itemView.findViewById(R.id.time_table_attendance_percentage);
        }
    }
}
