package com.example.remindme.ui.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.dataModels.Reminder;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.DeviceHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.ReminderModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivitySettings extends BaseActivity implements AdapterView.OnItemSelectedListener {

    final AppSettingsHelper settingsHelper = AppSettingsHelper.getInstance();
    public static final String THEME_CHANGE_INTENT_ACTION = "THEME_CHANGE_INTENT_ACTION";

    private TextView timeFormatTextView;
    private TextView dateFormatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityHelper.setTitle(this, getResources().getString(R.string.activitySettingsTitle));

        setUserInteracted(false);

        final TextView tv_brand = findViewById(R.id.tv_brand);
        tv_brand.setText(DeviceHelper.getInstance().getBrand());
        final TextView tv_model = findViewById(R.id.tv_model);
        tv_model.setText(DeviceHelper.getInstance().getModel());

        final TextView tv_os_signature = findViewById(R.id.tv_os_signature);
        tv_os_signature.setText(DeviceHelper.getInstance().getOperatingSystemSignature());

        final TextView tv_os_update_signature = findViewById(R.id.tv_os_update_signature);
        tv_os_update_signature.setText(DeviceHelper.getInstance().getOperatingSystemUpdateSignature());


        final AudioManager audioManager = OsHelper.getAudioManager(this);
        final SeekBar seeker_alarm_stream_volume = findViewById(R.id.seeker_alarm_stream_volume);
        seeker_alarm_stream_volume.setProgress(OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this)));
        seeker_alarm_stream_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                OsHelper.setAlarmVolumeInPercentage(audioManager, seekBar.getProgress());
                ToastHelper.showShort(ActivitySettings.this, "Alarm volume is set to " + seekBar.getProgress() + "%");
            }
        });

        final SwitchCompat sw_disable_all_reminders = findViewById(R.id.sw_disable_all_reminders);
        sw_disable_all_reminders.setChecked(settingsHelper.isDisableAllReminders());
        sw_disable_all_reminders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsHelper.setDisableAllReminders(isChecked);
                List<Reminder> list = ReminderModel.getActiveReminders(null);
                if (isChecked) {
                    for (int i = 0; i < list.size(); i++) {
                        ReminderModel reminderModel = ReminderModel.getInstance(list.get(i));
                        reminderModel.trySetEnabled(ActivitySettings.this, false);
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        ReminderModel reminderModel = ReminderModel.getInstance(list.get(i));
                        if (reminderModel.isEnabled()) {
                            if (reminderModel.trySetEnabled(ActivitySettings.this, true)) {
                                reminderModel.saveAndSetAlert(ActivitySettings.this, false);
                            }
                        }
                    }
                }
            }
        });


        final Button btn_os_setup_faqs = findViewById(R.id.btn_os_setup_faqs);
        btn_os_setup_faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent faqIntent = new Intent(Intent.ACTION_VIEW);
                faqIntent.setData(Uri.parse("https://www.google.co.in"));
                startActivity(faqIntent);
            }
        });


        final TextView tv_active_reminder_count = findViewById(R.id.tv_active_reminder_count);
        tv_active_reminder_count.setText(String.valueOf(ReminderModel.getActiveReminders(null).size()));

        final TextView tv_expired_reminder_count = findViewById(R.id.tv_expired_reminder_count);
        tv_expired_reminder_count.setText(String.valueOf(ReminderModel.getDismissedReminders(null).size()));

        final Spinner first_day_of_week_spinner = findViewById(R.id.first_day_of_week_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.first_day_of_week_options, R.layout.simple_spinner_dropdown_item);
        first_day_of_week_spinner.setAdapter(adapter);
        switch (settingsHelper.getFirstDayOfWeek()) {
            default:
            case Calendar.SUNDAY:
                first_day_of_week_spinner.setSelection(0);
                break;
            case Calendar.MONDAY:
                first_day_of_week_spinner.setSelection(1);
                break;
            case Calendar.SATURDAY:
                first_day_of_week_spinner.setSelection(2);
                break;
        }
        first_day_of_week_spinner.setOnItemSelectedListener(this);


        final Calendar currentDateCalendar = Calendar.getInstance();
        timeFormatTextView = findViewById(R.id.timeFormatTextView);
        timeFormatTextView.setText(StringHelper.toTime(currentDateCalendar.getTime()));
        final SwitchCompat sw_use_24_hour = findViewById(R.id.sw_use_24_hour);
        sw_use_24_hour.setChecked(settingsHelper.isUse24hourTime());
        sw_use_24_hour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUserInteracted()) {
                    settingsHelper.setUse24hourTime(isChecked);
                    timeFormatTextView.setText(StringHelper.toTime(Calendar.getInstance().getTime()));
                }
            }
        });


        dateFormatTextView = findViewById(R.id.dateFormatTextView);
        final SimpleDateFormat selectedDateFormat = new SimpleDateFormat(AppSettingsHelper.getInstance().getDateFormat(this), Locale.getDefault());
        dateFormatTextView.setText(selectedDateFormat.format(currentDateCalendar.getTime()));
        final Spinner dateFormatSpinner = findViewById(R.id.dateFormatSpinner);
        final String[] datePatterns = getResources().getStringArray(R.array.date_formats);
        final ArrayList<String> datePatternValues = new ArrayList<>();
        for (final String datePattern : datePatterns) {
            final SimpleDateFormat format = new SimpleDateFormat(datePattern, Locale.getDefault());
            datePatternValues.add(format.format(currentDateCalendar.getTime()));
        }
        final ArrayAdapter<String> dateFormatSpinnerAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_dropdown_item, datePatternValues);
        dateFormatSpinner.setAdapter(dateFormatSpinnerAdapter);
        dateFormatSpinner.setSelection(dateFormatSpinnerAdapter.getPosition(settingsHelper.getDateFormat(this)));
        dateFormatSpinner.setOnItemSelectedListener(this);


        final Spinner theme_spinner = findViewById(R.id.theme_spinner);
        final ArrayAdapter<CharSequence> theme_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.themes, R.layout.simple_spinner_dropdown_item);
        // adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        theme_spinner.setAdapter(theme_spinner_adapter);
        if (settingsHelper.getTheme() == AppSettingsHelper.Themes.LIGHT) {
            theme_spinner.setSelection(1);
        } else {
            theme_spinner.setSelection(0);
        }
        theme_spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (!isUserInteracted()) {
            return;
        }

        if (parent.getId() == R.id.first_day_of_week_spinner) {
            switch (position) {
                default:
                case 0:
                    settingsHelper.setFirstDayOfWeek(Calendar.SUNDAY);
                    break;

                case 1:
                    settingsHelper.setFirstDayOfWeek(Calendar.MONDAY);
                    break;

                case 2:
                    settingsHelper.setFirstDayOfWeek(Calendar.SATURDAY);
                    break;
            }
        } else if (parent.getId() == R.id.dateFormatSpinner) {
            settingsHelper.setDateFormat(getResources().getStringArray(R.array.date_formats)[position]);
            dateFormatTextView.setText(StringHelper.toWeekdayDate(this, Calendar.getInstance().getTime()));
        } else {

            if (position == 1) {
                if (isUserInteracted()) {
                    settingsHelper.setTheme(AppSettingsHelper.Themes.LIGHT);
                    final Intent sendThemeChanged = new Intent(THEME_CHANGE_INTENT_ACTION);
                    sendBroadcast(sendThemeChanged);
                    recreate();
                }
            } else {
                if (isUserInteracted()) {
                    settingsHelper.setTheme(AppSettingsHelper.Themes.BLACK);
                    final Intent sendThemeChanged = new Intent(THEME_CHANGE_INTENT_ACTION);
                    sendBroadcast(sendThemeChanged);
                    recreate();
                }
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}