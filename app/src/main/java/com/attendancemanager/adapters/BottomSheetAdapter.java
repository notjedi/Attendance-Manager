package com.attendancemanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.attendancemanager.R;
import com.attendancemanager.model.Subject;

import java.util.List;

public class BottomSheetAdapter extends ListAdapter<Subject, BottomSheetAdapter.BottomSheetViewHolder> {

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACk = new DiffUtil.ItemCallback<Subject>() {
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

    private OnAddButtonClickListener mListener;

    public BottomSheetAdapter() {
        super(DIFF_CALLBACk);
    }

    @NonNull
    @Override
    public BottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_item, parent, false);
        return new BottomSheetViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetViewHolder holder, int position) {

        holder.mSubjectName.setText(getItem(position).getSubjectName());
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnAddButtonClickListener {
        void addButton(int position);
    }

    public static class BottomSheetViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubjectName;
        private ImageButton mAddButton;

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
