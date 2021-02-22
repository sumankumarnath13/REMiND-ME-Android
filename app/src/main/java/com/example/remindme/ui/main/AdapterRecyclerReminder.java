package com.example.remindme.ui.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.remindme.R;
import com.example.remindme.ActivityReminderView;
import com.example.remindme.dataModels.ReminderActive;
import com.example.remindme.dataModels.ReminderDismissed;
import com.example.remindme.dataModels.ReminderMissed;
import com.example.remindme.util.UtilsAlarm;
import com.example.remindme.util.UtilsDateTime;
import java.text.ParseException;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class AdapterRecyclerReminder extends RecyclerView.Adapter<AdapterRecyclerReminder.ReminderHolder>{

    RealmResults<? extends RealmObject> _data;
    EnumReminderTypes reminderType;

    public AdapterRecyclerReminder(RealmResults<? extends  RealmObject> data, EnumReminderTypes type){
        this._data = data;
        this.reminderType = type;
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycler_reminder_item, parent, false);

        return new ReminderHolder(v, reminderType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReminderHolder holder, int position) {
        // create a new view
        TextView time = holder.linearLayout.findViewById(R.id.tv_reminder_time);
        TextView name = holder.linearLayout.findViewById(R.id.tv_reminder_name);
        TextView note = holder.linearLayout.findViewById(R.id.tv_reminder_truncated_note);
        SwitchCompat enabled = holder.linearLayout.findViewById(R.id.sw_reminder_enabled);
        ImageView img = holder.linearLayout.findViewById(R.id.img_snooze);
        TextView next_snooze = holder.linearLayout.findViewById(R.id.tv_reminder_next_snooze);

        final RealmObject reminder = _data.get(position);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.linearLayout.getContext(), ActivityReminderView.class);

                if(reminderType == EnumReminderTypes.Active){
                    final ReminderActive reminderActive = (ReminderActive) reminder;
                    i.putExtra("ID", reminderActive.id);
                    i.putExtra("NAME", reminderActive.name);
                    i.putExtra("NOTE", reminderActive.note);
                    if(reminderActive.next_snooze_id > 0){
                        i.putExtra("TIME", reminderActive.next_snooze_id);
                    }
                    else{
                        i.putExtra("TIME", reminderActive.id);
                    }
                    i.putExtra("FROM", "ACTIVE");
                }
                else if(reminderType == EnumReminderTypes.Missed){
                    final ReminderMissed reminderMissed = (ReminderMissed)reminder;
                    i.putExtra("ID", reminderMissed.id);
                    i.putExtra("NAME", reminderMissed.name);
                    i.putExtra("NOTE", reminderMissed.note);
                    i.putExtra("FROM", "MISSED");
                }
                else{
                    final ReminderDismissed reminderDismissed = (ReminderDismissed)reminder;
                    i.putExtra("ID", reminderDismissed.id);
                    i.putExtra("NAME", reminderDismissed.name);
                    i.putExtra("NOTE", reminderDismissed.note);
                    i.putExtra("FROM", "DISMISSED");
                }

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
                    final ReminderActive reminderActive = (ReminderActive) reminder;
                    reminderActive.isEnable = enabled;
                    realm.insertOrUpdate(reminderActive);
                    if(enabled){
                        try {
                            UtilsAlarm.set(holder.linearLayout.getContext(), reminderActive);
                        }
                        catch (ParseException e) {
                            Toast.makeText(holder.linearLayout.getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        UtilsAlarm.unSet(holder.linearLayout.getContext(), reminderActive.id);
                    }
                }
            });
            }
        });

        try {
            if(reminderType == EnumReminderTypes.Active) {
                final ReminderActive reminderActive = (ReminderActive) reminder;
                String str_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminderActive.id));
                time.setText(str_time);
                if (reminderActive.next_snooze_id > 0) {
                    next_snooze.setText(UtilsDateTime.toTimeString(UtilsDateTime.toDate(reminderActive.next_snooze_id)));
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setVisibility(View.GONE);
                }
                enabled.setChecked(reminderActive.isEnable);
                name.setText(reminderActive.name);
                note.setText(reminderActive.note);
            }
            else if (holder.reminderType == EnumReminderTypes.Missed) {
                final ReminderMissed reminderMissed = (ReminderMissed) reminder;
                String str_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminderMissed.id));
                time.setText(str_time);
                time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_danger));
                enabled.setVisibility(View.GONE);
                name.setText(reminderMissed.name);
                note.setText(reminderMissed.note);
            }
            else {
                final ReminderDismissed reminderDismissed = (ReminderDismissed) reminder;
                String str_time = UtilsDateTime.toTimeDateString(UtilsDateTime.toDate(reminderDismissed.id));
                time.setText(str_time);
                time.setTextColor(holder.linearLayout.getResources().getColor(R.color.text_gray2));
                enabled.setVisibility(View.GONE);
                name.setText(reminderDismissed.name);
                note.setText(reminderDismissed.note);
            }
        }
        catch (ParseException e) {
            time.setText("E!");
            Toast.makeText(holder.linearLayout.getContext(), "PARSE ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public static class ReminderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public EnumReminderTypes reminderType;
        public ReminderHolder(LinearLayout v, EnumReminderTypes t) {
            super(v);
            linearLayout = v;
            reminderType = t;
        }
    }
}
