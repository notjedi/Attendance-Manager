package com.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private RecyclerView recyclerView;
    private Preference editSubject;

    private static final String TAG = "SettingsFragment";
    private static final String EDIT_SUBJECTS = "key_edit_subjects";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editSubject = findPreference(getString(R.string.key_edit_subjects));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editSubject.setOnPreferenceClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            ExpandableBottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    /* Scrolled down */
                    bottomBar.hide();
                } else {
                    /* Scrolled up */
                    bottomBar.show();
                }
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        switch (preference.getKey()) {
            case EDIT_SUBJECTS:
                Intent intent = new Intent(getContext(), EditSubjectActivity.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected preference key" + preference.getKey());
        }

        return true;
    }
}