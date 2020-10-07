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
import com.example.remindme.dataModels.ReminderMissed;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMissedReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMissedReminder extends Fragment {

    public FragmentMissedReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MissedReminderFragmentActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMissedReminder newInstance() {
        FragmentMissedReminder fragment = new FragmentMissedReminder();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_missed_reminder, container, false);
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

/*    private void refresh() throws ParseException {
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<ReminderMissed> reminders = realm.where(ReminderMissed.class).findAll();

        final LinearLayout layout = getView().findViewById(R.id.linear_view_missed);

        layout.removeAllViewsInLayout();

        final int N = reminders.size(); // total number of textviews to add

        final TextView[] myTextViews = new TextView[N]; // create an empty array;

        for(int i=0; i<N; i++){

            final ReminderMissed r = reminders.get(i);

            final TextView t = new TextView(this.getContext());

            final String date = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(r.id));
            final String name = r.name == null ? "" : "\n" + r.name;
            final String note = r.note == null ? "" : "\n" + r.note;

            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getContext(), ActivityReminderView.class);
                    i.putExtra("ID", r.id);
                    i.putExtra("NAME", r.name);
                    i.putExtra("NOTE", r.note);
                    i.putExtra("FROM", "MISSED");
                    startActivity(i);
                }
            });

            Spannable spannable = new SpannableString(date + name + note);

            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_danger)), 0, date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_white)), date.length(),
                    date.length() + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_dark)), date.length() + name.length(),
                    date.length() + name.length() + note.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            t.setText(spannable);

            //t.setTextColor(getResources().getColor(R.color.text_dark));
            // set some properties of rowTextView or something
            //t.setText("This is row #" + i);
            layout.addView(t);

            // save a reference to the textview for later
            myTextViews[i] = t;
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        // specify an adapter (see also next example)
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReminderMissed> data = realm.where(ReminderMissed.class).findAll();
        RecyclerView.Adapter mAdapter = new AdapterRecyclerReminder(data, EnumReminderTypes.Missed);
        recyclerView.setAdapter(mAdapter);
    }
}