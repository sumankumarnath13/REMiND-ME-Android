package com.example.remindme.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindme.R;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.viewModels.ReminderModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentActiveReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentActiveReminder extends Fragment {

    public FragmentActiveReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment UpcomingReminderFragmentActivity.
     */
    public static FragmentActiveReminder newInstance() {
        return new FragmentActiveReminder();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_reminder, container, false);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycler_reminders);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // specify an adapter (see also next example)
        List<ReminderActive> data = ReminderModel.getAll();
        RecyclerView.Adapter mAdapter = new AdapterRecyclerReminder(data, EnumReminderTypes.Active);
        recyclerView.setAdapter(mAdapter);
    }
}