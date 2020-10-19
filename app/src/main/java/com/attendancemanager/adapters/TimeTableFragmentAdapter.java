package com.attendancemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimeTableFragmentAdapter extends RecyclerView.Adapter<TimeTableFragmentAdapter.TimeTableViewHolder> {

    private List<Subject> subjectList;

    public TimeTableFragmentAdapter() {
        /* Using ListAdapter caused weird issues so i shifted to the old RecyclerView.Adapter,
        but it has it's trade-offs cause i am forced to use notifyDatasetChanged() every time */
        this.subjectList = new ArrayList<>();
    }

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_table, parent, false);
        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {

        Subject subject = subjectList.get(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round((
                (float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mPosition.setText(String.valueOf(position + 1));
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendancePercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
        notifyDataSetChanged();
    }

    public static class TimeTableViewHolder extends RecyclerView.ViewHolder {

        private final TextView mPosition;
        private final TextView mSubjectName;
        private final TextView mAttendancePercentage;

        public TimeTableViewHolder(@NonNull View itemView) {
            super(itemView);

            mPosition = itemView.findViewById(R.id.subject_position);
            mSubjectName = itemView.findViewById(R.id.time_table_subject_name);
            mAttendancePercentage = itemView.findViewById(R.id.time_table_attendance_percentage);
        }
    }
}
