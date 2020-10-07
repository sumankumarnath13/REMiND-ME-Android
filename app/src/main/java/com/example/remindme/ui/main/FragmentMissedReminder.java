package com.example.remindme.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindme.R;
import com.example.remindme.ActivityReminderView;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMissedReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMissedReminder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentMissedReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MissedReminderFragmentActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMissedReminder newInstance(String param1, String param2) {
        FragmentMissedReminder fragment = new FragmentMissedReminder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_missed_reminder, container, false);
    }

    private void refresh() throws ParseException {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            this.refresh();
        } catch (ParseException e) {
            Toast.makeText(getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}