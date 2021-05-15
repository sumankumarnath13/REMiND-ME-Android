package com.example.remindme.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.viewModels.ReminderModel;

public class FragmentDismissedReminder extends Fragment {

    public FragmentDismissedReminder() {
        // Required empty public constructor
    }

    public static FragmentDismissedReminder newInstance() {
        return new FragmentDismissedReminder();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dismissed_reminder, container, false);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.finished_reminders_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final AdapterRecyclerReminder mAdapter = new AdapterRecyclerReminder(ReminderModel.getDismissedReminders(null),
                (AdapterRecyclerReminder.iDataChangeListener) getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    private String queryString;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.context_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) menuItem.getActionView();
        sv.setQueryHint("Enter name to find");

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryString = newText;
                query();
                return true;
            }
        });
    }

    public void query() {
        final AdapterRecyclerReminder mAdapter = new AdapterRecyclerReminder(ReminderModel.getDismissedReminders(queryString),
                (AdapterRecyclerReminder.iDataChangeListener) getActivity());
        recyclerView.setAdapter(mAdapter);
    }
}