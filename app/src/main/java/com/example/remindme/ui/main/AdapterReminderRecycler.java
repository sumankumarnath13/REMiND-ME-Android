package com.example.remindme.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.remindme.R;
import com.example.remindme.ActivityReminderView;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterReminderRecycler extends RecyclerView.Adapter<AdapterReminderRecycler.ReminderHolder>{

    RealmResults<ReminderActive> _data;

    public AdapterReminderRecycler(RealmResults<ReminderActive> data){
        this._data = data;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_reminder_recycler_item, parent, false);

        ReminderHolder vh = new ReminderHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderHolder holder, int position) {
        // create a new view
        TextView time = holder.linearLayout.findViewById(R.id.tv_reminder_time);
        TextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        TextView note = holder.linearLayout.findViewById(R.id.tv_reminder_truncated_note);
        Switch enabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        ImageView img = holder.linearLayout.findViewById(R.id.img_snooze);
        TextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);

        final ReminderActive reminder = _data.get(position);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(holder.linearLayout.getContext(), ActivityReminderView.class);
            i.putExtra("ID", reminder.id);
            if(reminder.next_snooze_id > 0){
                i.putExtra("TIME", reminder.next_snooze_id);
            }
            else{
                i.putExtra("TIME", reminder.id);
            }
            i.putExtra("NAME", reminder.name);
            i.putExtra("NOTE", reminder.note);
            i.putExtra("FROM", "ACTIVE");
            holder.linearLayout.getContext().startActivity(i);
            }
        });

        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            final boolean enabled = b;
            Realm r = Realm.getDefaultInstance();
            r.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                reminder.enabled = enabled;
                realm.insertOrUpdate(reminder);
                if(enabled){
                    try {
                        UtilsAlarm.set(holder.linearLayout.getContext(), reminder);
                    }
                    catch (ParseException e) {
                        Toast.makeText(holder.linearLayout.getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    UtilsAlarm.unSet(holder.linearLayout.getContext(), reminder.id);
                }
                }
            });
            }
        });

        try {
            String str_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminder.id));
/*            Spannable spannable = new SpannableString(str_time);

            spannable.setSpan(new ForegroundColorSpan(holder.table.getResources().getColor(R.color.text_success)), 0, str_time.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.5f), 0, str_time.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new ForegroundColorSpan(holder.table.getResources().getColor(R.color.text_white)), str_time.length(),
                    str_time.length() + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/

            time.setText(str_time);
        }
        catch (ParseException e) {
            time.setText("E!");
            Toast.makeText(holder.linearLayout.getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if(reminder.next_snooze_id > 0){
            try {
                next_snooze.setText(UtilsDateTime.toTimeString(UtilsDateTime.toDate(reminder.next_snooze_id)));
            } catch (ParseException e) {
                Toast.makeText(holder.linearLayout.getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            img.setVisibility(View.VISIBLE);
        }
        else{
            img.setVisibility(View.GONE);
        }
        enabled.setChecked(reminder.enabled);
        name.setText(reminder.name);
        note.setText(reminder.note);
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public ReminderHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
        }
    }
}
