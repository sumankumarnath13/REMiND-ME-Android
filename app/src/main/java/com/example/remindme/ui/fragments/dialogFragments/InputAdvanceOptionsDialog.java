package com.example.remindme.ui.fragments.dialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.controllers.RingingController;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.dialogFragments.common.DialogFragmentBase;
import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.RingingModel;
import com.example.remindme.viewModels.SnoozeModel;
import com.example.remindme.viewModels.factories.AlertViewModelFactory;

public class InputAdvanceOptionsDialog extends DialogFragmentBase
        implements
        SnoozeDialog.ISnoozeInputDialogListener,
        AdapterView.OnItemSelectedListener {

    public static final String TAG = "InputAdvanceOptionsDialog";

    private SnoozeDialog snoozeDialog;

    private SnoozeDialog getSnoozeDialog() {
        if (snoozeDialog == null) {
            snoozeDialog = new SnoozeDialog();
        }
        return snoozeDialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RINGTONE_DIALOG_REQ_CODE && data != null) {
            final Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                getModel().getRingingModel().setRingToneUri(uri);
            }
            refresh();
        }
    }

    @Override
    public void onSetListenerSnoozeModel(SnoozeModel model) {
        getModel().setSnoozeModel(model);
        refresh();
    }

    @Override
    public SnoozeModel onGetListenerSnoozeModel() {
        return getModel().getSnoozeModel();
    }

    public interface IInputAdvanceOptions {

        void setInputAdvanceOptionsModel(AlertModel model);

        AlertModel getInputAdvanceOptionsModel();

    }

    private IInputAdvanceOptions listener;

    protected IInputAdvanceOptions getListener() {
        if (listener == null) {
            listener = super.getListener(IInputAdvanceOptions.class);
        }
        return listener;
    }

    private AlertModel model;

    private AlertModel getModel() {
        return model;
    }

    private RingingController getRingingController() {
        if (ringingController == null || !isPlayingTone) { // "!isPlayingTone" this condition will ensure new instance of RingingController with updated ReminderModel tone URI.
            ringingController = new RingingController(getContext(), getModel().getRingingModel().getRingToneUri());
        }
        return ringingController;
    }

    private void startVibrating() {
        getRingingController().vibrateOnce(RingingModel.convertToVibrateFrequency(getModel().getRingingModel().getVibratePattern()));
    }

    private void startTone() {
        getRingingController().startTone(getModel().getRingingModel().isIncreaseVolumeGradually(), getModel().getRingingModel().getAlarmVolumePercentage());
        isPlayingTone = true;
    }

    private void stopTone() {
        if (ringingController != null) {
            ringingController.stopRinging(); // Stop ring stops both tone and vibration if is playing.
        }
        isPlayingTone = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.ring_duration_spinner) {
            getModel().getRingingModel().setAlarmRingDuration(RingingModel.convertToAlarmRingDuration(position));
        } else if (parent.getId() == R.id.vibrate_pattern_spinner) {
            getModel().getRingingModel().setVibratePattern(RingingModel.convertToVibratePattern(position));
            if (isAwaitingVibrationSelection) {
                isAwaitingVibrationSelection = false;
            } else {
                startVibrating();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        stopTone();
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getListener() == null) {
            ToastHelper.showError(getContext(), "Dialog listener is not set!");
            dismiss();
            return;
        }

        isAwaitingVibrationSelection = true;

        model = new ViewModelProvider(this,
                new AlertViewModelFactory(getListener()
                        .getInputAdvanceOptionsModel())).get(AlertModel.class);
    }

    private static final int RINGTONE_DIALOG_REQ_CODE = 117;
    private AppCompatTextView tv_reminder_tone_summary;
    private SwitchCompat sw_reminder_snooze;
    private AppCompatTextView tv_reminder_snooze_summary;
    private AppCompatSeekBar seeker_alarm_volume;
    private SwitchCompat sw_gradually_increase_volume;
    private SwitchCompat sw_reminder_tone;
    private SwitchCompat sw_reminder_vibrate;
    private AppCompatSpinner ring_duration_spinner;
    private AppCompatSpinner vibrate_pattern_spinner;
    private AppCompatImageButton imgBtnPlayStop;
    private AppCompatButton btnSetDefaultTone;
    private boolean isPlayingTone;
    private int deviceAlarmVolume;
    private LinearLayoutCompat alarm_only_layout;
    private RingingController ringingController;
    private boolean isAwaitingVibrationSelection;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (activity == null)
            return builder.create();
        final LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_fragment_input_advance_options, null);
        builder.setView(view).setTitle("Reminder advance options")
                .setPositiveButton(R.string.acton_dialog_positive, (dialog, which) -> getListener().setInputAdvanceOptionsModel(model))
                .setNegativeButton(R.string.acton_dialog_negative, (dialog, which) -> {

                });

        if (getContext() == null)
            return builder.create();

        alarm_only_layout = view.findViewById(R.id.alarm_only_layout);

        tv_reminder_snooze_summary = view.findViewById(R.id.tv_reminder_snooze_summary);
        sw_reminder_snooze = view.findViewById(R.id.sw_reminder_snooze);
        sw_reminder_snooze.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isRefreshing())
                return;

            getModel().getSnoozeModel().setEnable(sw_reminder_snooze.isChecked());
            refresh();
        });

        final LinearLayoutCompat mnu_reminder_snooze = view.findViewById(R.id.snooze_input_layout);
        mnu_reminder_snooze.setOnClickListener(v -> getSnoozeDialog().show(getParentFragmentManager(), SnoozeDialog.TAG));

        final LinearLayoutCompat mnu_reminder_tone = view.findViewById(R.id.tone_input_layout);
        mnu_reminder_tone.setOnClickListener(v -> {
            final Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, getModel().getRingingModel().getRingToneUri());
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, RINGTONE_DIALOG_REQ_CODE);
        });

        tv_reminder_tone_summary = view.findViewById(R.id.tv_reminder_tone_summary);
        sw_reminder_tone = view.findViewById(R.id.sw_reminder_tone);
        sw_reminder_tone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isRefreshing())
                return;

            getModel().getRingingModel().setToneEnabled(isChecked);
            refresh();
        });

        imgBtnPlayStop = view.findViewById(R.id.imgBtnPlayStop);
        imgBtnPlayStop.setOnClickListener(v -> {
            if (isPlayingTone) {
                stopTone();
            } else {
                startTone();
            }
            refresh();
        });

        btnSetDefaultTone = view.findViewById(R.id.btnSetDefaultTone);
        btnSetDefaultTone.setOnClickListener(v -> {
            getModel().getRingingModel().setDefaultRingTone();
            refresh();
        });

        deviceAlarmVolume = OsHelper.getAlarmVolumeInPercentage(OsHelper.getAudioManager(getContext()));
        seeker_alarm_volume = view.findViewById(R.id.seeker_alarm_volume);
        seeker_alarm_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE)
                        seekBar.setProgress(RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getModel().getRingingModel().setAlarmVolumePercentage(seekBar.getProgress());
                ToastHelper.showShort(getContext(), "Alarm will ring at " + getModel().getRingingModel().getAlarmVolumePercentage() + "% volume");
            }
        });

        sw_gradually_increase_volume = view.findViewById(R.id.sw_gradually_increase_volume);
        sw_gradually_increase_volume.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isRefreshing())
                return;

            getModel().getRingingModel().setIncreaseVolumeGradually(sw_gradually_increase_volume.isChecked());
            refresh();
        });

        sw_reminder_vibrate = view.findViewById(R.id.sw_reminder_vibrate);
        sw_reminder_vibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isRefreshing())
                return;

            getModel().getRingingModel().setVibrationEnabled(isChecked);
            refresh();
        });

        ring_duration_spinner = view.findViewById(R.id.ring_duration_spinner);
        final ArrayAdapter<CharSequence> ring_duration_adapter = ArrayAdapter.createFromResource(getContext(), R.array.values_ring_duration, R.layout.item_dropdown_fragment_simple_spinner);
        ring_duration_spinner.setAdapter(ring_duration_adapter);
        ring_duration_spinner.setOnItemSelectedListener(InputAdvanceOptionsDialog.this);

        vibrate_pattern_spinner = view.findViewById(R.id.vibrate_pattern_spinner);
        final ArrayAdapter<CharSequence> vibrate_pattern_adapter = ArrayAdapter.createFromResource(getContext(), R.array.values_vibration_pattern, R.layout.item_dropdown_fragment_simple_spinner);
        vibrate_pattern_spinner.setAdapter(vibrate_pattern_adapter);
        vibrate_pattern_spinner.setOnItemSelectedListener(InputAdvanceOptionsDialog.this);

        refresh();

        return builder.create();
    }

    @Override
    protected void onUIRefresh() {
        // No radio group wont work for the given layout. So resetting programmatically is required.
        if (getModel().isReminder()) {
            alarm_only_layout.setVisibility(View.GONE);
        } else {
            alarm_only_layout.setVisibility(View.VISIBLE);
        }
        tv_reminder_snooze_summary.setText(getModel().getSnoozeModel().toString());
        sw_reminder_snooze.setChecked(getModel().getSnoozeModel().isEnable());
        sw_reminder_tone.setChecked(getModel().getRingingModel().isToneEnabled());
        if (getModel().getRingingModel().getRingToneUri() == null) {
            final Uri alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            final Ringtone alarmTone = RingtoneManager.getRingtone(getContext(), alarmToneUri);
            tv_reminder_tone_summary.setText(alarmTone.getTitle(getContext()));
            btnSetDefaultTone.setVisibility(View.INVISIBLE);
        } else {
            final Ringtone ringtone = RingtoneManager.getRingtone(getContext(), getModel().getRingingModel().getRingToneUri());
            tv_reminder_tone_summary.setText(ringtone.getTitle(getContext()));
            btnSetDefaultTone.setVisibility(View.VISIBLE);
        }
        if (getModel().getRingingModel().isToneEnabled()) {
            sw_gradually_increase_volume.setEnabled(true);
            sw_gradually_increase_volume.setChecked(getModel().getRingingModel().isIncreaseVolumeGradually());

            imgBtnPlayStop.setEnabled(true);
            btnSetDefaultTone.setEnabled(true);

            seeker_alarm_volume.setEnabled(true);
            tv_reminder_tone_summary.setEnabled(true);

            ring_duration_spinner.setEnabled(true);
            ring_duration_spinner.setSelection(RingingModel.convertToAlarmRingDuration(getModel().getRingingModel().getAlarmRingDuration()), false);
        } else {
            sw_gradually_increase_volume.setEnabled(false);
            sw_gradually_increase_volume.setChecked(false);

            imgBtnPlayStop.setEnabled(false);
            btnSetDefaultTone.setEnabled(false);

            seeker_alarm_volume.setEnabled(false);
            tv_reminder_tone_summary.setEnabled(false);
            ring_duration_spinner.setEnabled(false);
        }

        if (getModel().getRingingModel().getAlarmVolumePercentage() == 0) {
            seeker_alarm_volume.setProgress(deviceAlarmVolume);
        } else {
            seeker_alarm_volume.setProgress(getModel().getRingingModel().getAlarmVolumePercentage());
        }

        if (isPlayingTone) {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play_stop);
        } else {
            imgBtnPlayStop.setImageResource(R.drawable.ic_play);
        }

        tv_reminder_tone_summary.setText(getModel().getRingingModel().getRingToneUriSummary(getContext()));
        sw_reminder_vibrate.setChecked(getModel().getRingingModel().isVibrationEnabled());

        vibrate_pattern_spinner.setSelection(RingingModel.convertToVibratePattern(getModel().getRingingModel().getVibratePattern()), false);
        vibrate_pattern_spinner.setEnabled(getModel().getRingingModel().isVibrationEnabled());
    }
}
