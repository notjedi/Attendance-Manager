package com.attendancemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class EditSubjectAdapter extends RecyclerView.Adapter<EditSubjectAdapter.EditSubjectListViewHolder> {

    private List<Subject> mSubjectList;
    private Context mContext;

    public EditSubjectAdapter(List<Subject> mSubjectList, Context mContext) {
        this.mSubjectList = mSubjectList;
        this.mContext = mContext;
    }

    public static class EditSubjectListViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubjectName, mProgressPercentage, mAttendedClasses;
        private ProgressBar mProgressBar;

        public EditSubjectListViewHolder(@NonNull View itemView) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.edit_subject_name);
            mProgressPercentage = itemView.findViewById(R.id.edit_subject_progressbar_percentage);
            mAttendedClasses = itemView.findViewById(R.id.edit_total_classes_attended);
            mProgressBar = itemView.findViewById(R.id.edit_subject_attendance_progressbar);
        }
    }

    @NonNull
    @Override
    public EditSubjectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.edit_subject_list_row, parent, false);
        return new EditSubjectListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditSubjectAdapter.EditSubjectListViewHolder holder, int position) {

        Subject subject = mSubjectList.get(position);
        int percentage = subject.getTotalClasses() == 0 ? 0 : Math.round(((float) subject.getAttendedClasses() / (float) subject.getTotalClasses()) * 100);

        holder.mProgressBar.setProgress(percentage);
        holder.mSubjectName.setText(subject.getSubjectName());
        holder.mAttendedClasses.setText(String.format(mContext.getResources().getString(R.string.attended_info_template), Integer.toString(subject.getAttendedClasses()), Integer.toString(subject.getTotalClasses())));
        holder.mProgressPercentage.setText(String.format(Locale.US, "%d%%", percentage));
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
    }
}
