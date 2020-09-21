package com.attendancemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.data.Subject;

import java.util.List;
import java.util.Locale;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder> {

    private List<Subject> mSubjectList;

    public TimeTableAdapter(List<Subject> mSubjectList) {
        this.mSubjectList = mSubjectList;
    }

    @NonNull
    @Override
    public TimeTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_table_item, parent, false);
        return new TimeTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeTableViewHolder holder, int position) {

        Subject subject = mSubjectList.get(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round(((float) subject.getAttendedClasses()
                / (float) subject.getTotalClasses()) * 100);

        holder.mPosition.setText(String.valueOf(position + 1));
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendancePercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
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
