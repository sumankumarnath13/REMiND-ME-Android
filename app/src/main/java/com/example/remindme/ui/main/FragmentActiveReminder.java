package com.example.remindme.ui.main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remindme.R;
import com.example.remindme.dataModels.ReminderActive;

import io.realm.Realm;
import io.realm.RealmResults;

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
    // TODO: Rename and change types and number of parameters
    public static FragmentActiveReminder newInstance() {
        FragmentActiveReminder fragment = new FragmentActiveReminder();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_reminder, container, false);

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recycler_active_reminders);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return  view;
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RealmResults<ReminderActive> data;

    @Override
    public void onResume() {
        super.onResume();
        // specify an adapter (see also next example)
        Realm realm = Realm.getDefaultInstance();
        data = realm.where(ReminderActive.class).findAll();
        mAdapter = new AdapterReminderRecycler(data);
        recyclerView.setAdapter(mAdapter);
    }
}