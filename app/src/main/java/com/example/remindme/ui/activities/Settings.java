package com.example.remindme.ui.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.DeviceHelper;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.viewModels.AlertModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Settings extends ActivityBase implements AdapterView.OnItemSelectedListener {

    final AppSettingsHelper settingsHelper = AppSettingsHelper.getInstance();
    public static final String THEME_CHANGE_INTENT_ACTION = "THEME_CHANGE_INTENT_ACTION";

    private AppCompatTextView timeFormatTextView;
    private AppCompatTextView dateFormatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setActivityTitle(getResources().getString(R.string.menu_label_app_settings));

        setUserInteracted(false);

        final AppCompatTextView tv_brand = findViewById(R.id.tv_brand);
        tv_brand.setText(DeviceHelper.getInstance().getBrand());
        final AppCompatTextView tv_model = findViewById(R.id.tv_model);
        tv_model.setText(DeviceHelper.getInstance().getModel());

        final AppCompatTextView tv_os_signature = findViewById(R.id.tv_os_signature);
        tv_os_signature.setText(DeviceHelper.getInstance().getOperatingSystemSignature());

        final AppCompatTextView tv_os_update_signature = findViewById(R.id.tv_os_update_signature);
        tv_os_update_signature.setText(DeviceHelper.getInstance().getOperatingSystemUpdateSignature());


        final AudioManager audioManager = OsHelper.getAudioManager(this);
        final AppCompatSeekBar seeker_alarm_stream_volume = findViewById(R.id.seeker_alarm_stream_volume);
        seeker_alarm_stream_volume.setProgress(OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(this)));
        seeker_alarm_stream_volume.setOnSeekBarChangeListener(new AppCompatSeekBar.OnSeekBarChangeListener() {
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
                ToastHelper.showShort(Settings.this, "Alarm volume is set to " + seekBar.getProgress() + "%");
            }
        });

        final SwitchCompat sw_disable_all_reminders = findViewById(R.id.sw_disable_all_reminders);
        sw_disable_all_reminders.setChecked(settingsHelper.isDisableAllReminders());
        sw_disable_all_reminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsHelper.setDisableAllReminders(isChecked);
            final List<AlertModel> list = AlertModel.getActiveReminders(null);
            if (isChecked) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).trySetEnabled(Settings.this, false);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isEnabled()) {
                        if (list.get(i).trySetEnabled(Settings.this, true)) {
                            list.get(i).saveAndSetAlert(Settings.this, false);
                        }
                    }
                }
            }
        });


        final AppCompatButton btn_os_setup_faqs = findViewById(R.id.btn_os_setup_faqs);
        btn_os_setup_faqs.setOnClickListener(v -> {
            final Intent faqIntent = new Intent(Intent.ACTION_VIEW);
            faqIntent.setData(Uri.parse("https://www.google.co.in"));
            startActivity(faqIntent);
        });


        final AppCompatTextView tv_active_reminder_count = findViewById(R.id.tv_active_reminder_count);
        tv_active_reminder_count.setText(String.valueOf(AlertModel.getActiveReminders(null).size()));

        final AppCompatTextView tv_expired_reminder_count = findViewById(R.id.tv_expired_reminder_count);
        tv_expired_reminder_count.setText(String.valueOf(AlertModel.getDismissedReminders(null).size()));

        final AppCompatSpinner first_day_of_week_spinner = findViewById(R.id.first_day_of_week_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.values_first_day_of_week, R.layout.item_dropdown_fragment_simple_spinner);
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
        timeFormatTextView.setText(StringHelper.toTimeAmPm(currentDateCalendar.getTime()));
        final SwitchCompat sw_use_24_hour = findViewById(R.id.sw_use_24_hour);
        sw_use_24_hour.setChecked(settingsHelper.isUse24hourTime());
        sw_use_24_hour.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUserInteracted()) {
                settingsHelper.setUse24hourTime(isChecked);
                timeFormatTextView.setText(StringHelper.toTimeAmPm(Calendar.getInstance().getTime()));
            }
        });


        dateFormatTextView = findViewById(R.id.dateFormatTextView);
        dateFormatTextView.setText(StringHelper.toWeekdayDate(this, Calendar.getInstance().getTime()));

        final AppCompatSpinner dateFormatSpinner = findViewById(R.id.dateFormatSpinner);
        final List<String> datePatterns = Arrays.asList(getResources().getStringArray(R.array.values_date_format));
        final ArrayList<String> datePatternValues = new ArrayList<>();
        for (final String datePattern : datePatterns) {
            final SimpleDateFormat format = new SimpleDateFormat(datePattern, Locale.getDefault());
            datePatternValues.add(format.format(currentDateCalendar.getTime()));
        }
        final ArrayAdapter<String> dateFormatSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_dropdown_fragment_simple_spinner, datePatternValues);
        dateFormatSpinner.setAdapter(dateFormatSpinnerAdapter);

        // Finding position from adapter will be wrong as its the current time formatted values that goes inside the adapter
        dateFormatSpinner.setSelection(datePatterns.indexOf(settingsHelper.getDateFormat(this)));
        //-----------------------

        dateFormatSpinner.setOnItemSelectedListener(this);


        final AppCompatSpinner theme_spinner = findViewById(R.id.theme_spinner);
        final ArrayAdapter<CharSequence> theme_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.values_theme, R.layout.item_dropdown_fragment_simple_spinner);
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
            settingsHelper.setDateFormat(getResources().getStringArray(R.array.values_date_format)[position]);
            dateFormatTextView.setText(StringHelper.toWeekdayDate(this, Calendar.getInstance().getTime()));
        } else {

            if (position == 1) {
                if (isUserInteracted()) {
                    settingsHelper.setTheme(AppSettingsHelper.Themes.LIGHT);
                    final Intent sendThemeChanged = new Intent(THEME_CHANGE_INTENT_ACTION);
                    sendBroadcast(sendThemeChanged);
                }
            } else {
                if (isUserInteracted()) {
                    settingsHelper.setTheme(AppSettingsHelper.Themes.BLACK);
                    final Intent sendThemeChanged = new Intent(THEME_CHANGE_INTENT_ACTION);
                    sendBroadcast(sendThemeChanged);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}