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

public class BottomSheetAdapter extends ListAdapter<Subject, BottomSheetAdapter.BottomSheetViewHolder> {

    private static final DiffUtil.ItemCallback<Subject> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subject>() {
        /* Using RecyclerView.ListAdapter and DiffUtil to pass on data changes from LiveData to RecyclerView */

        @Override
        public boolean areItemsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subject oldItem, @NonNull Subject newItem) {
            /* No need to check if attended classes and total classes are same */
            return oldItem.getSubjectName().equals(newItem.getSubjectName());
        }
    };
    private OnAddButtonClickListener mListener;

    public BottomSheetAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public BottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bottom_sheet, parent, false);
        return new BottomSheetViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetViewHolder holder, int position) {

        holder.mSubjectName.setText(getItem(position).getSubjectName());
    }

    public void setOnAddButtonClickListener(OnAddButtonClickListener mListener) {
        this.mListener = mListener;
    }

    public Subject getSubjectAt(int position) {
        return getItem(position);
    }

    public interface OnAddButtonClickListener {
        void addButton(int position);
    }

    public static class BottomSheetViewHolder extends RecyclerView.ViewHolder {

        private final TextView mSubjectName;

        public BottomSheetViewHolder(@NonNull View itemView, OnAddButtonClickListener listener) {
            super(itemView);

            mSubjectName = itemView.findViewById(R.id.bottom_sheet_subject_name);
            ImageButton mAddButton = itemView.findViewById(R.id.bottom_sheet_add_button);

            mAddButton.setOnClickListener(v -> listener.addButton(getBindingAdapterPosition()));
        }
    }
}
