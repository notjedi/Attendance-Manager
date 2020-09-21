package com.attendancemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder> {

    private List<Subject> mSubjectList;
    private OnAddButtonClickListener mListener;

    public BottomSheetAdapter(List<Subject> mSubjectList) {
        this.mSubjectList = mSubjectList;
    }

    @NonNull
    @Override
    public BottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item, parent, false);
        return new BottomSheetViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetViewHolder holder, int position) {

        holder.mSubjectName.setText(mSubjectList.get(position).getSubjectName());
    }

    @Override
    public int getItemCount() {
        return mSubjectList.size();
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnAddButtonClickListener{
        void addButton(int position);
    }

    public static class BottomSheetViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubjectName;
        private ImageButton mAddButton;

        private static List<Subject> subjectList;

        public static void setSubjectList(List<Subject> subjects) {
            subjectList = subjects;
        }

        public BottomSheetViewHolder(@NonNull View itemView, OnAddButtonClickListener listener) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.bottom_sheet_subject_name);
            mAddButton = itemView.findViewById(R.id.bottom_sheet_add_button);

            mAddButton.setOnClickListener(v -> {
                listener.addButton(getAdapterPosition());
            });
        }
    }
}
