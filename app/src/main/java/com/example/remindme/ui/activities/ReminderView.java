package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.helpers.StringHelper;
import com.example.remindme.helpers.ToastHelper;
import com.example.remindme.ui.fragments.common.FabContextMenu;
import com.example.remindme.ui.fragments.dialogFragments.MissedAlertsDialog;
import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.RingingModel;
import com.example.remindme.viewModels.factories.AlertViewModelFactory;

import java.util.Locale;

public class ReminderView extends ActivityBase
        implements
        FabContextMenu.iFabContextMenuListener,
        MissedAlertsDialog.IMissedAlertsDialogListener {

    private boolean isMenuExpanded;

    private AppCompatTextView tv_reminder_time;
    private AppCompatTextView tv_reminder_AmPm;
    private AppCompatTextView tv_reminder_date;
    private AppCompatTextView tv_reminder_name;
    private LinearLayoutCompat ly_note_summary_header;
    private AppCompatTextView tv_reminder_note;

    private SwitchCompat switchEnabled;
    private AlertModel alertModel;
    private AppCompatTextView tv_expired;
    private AppCompatTextView next_snooze;
    private AppCompatButton btn_reminder_dismiss;

    private static final String STATUS_OFF = "OFF";
    private static final String C_ACTION_EDIT = "EDIT";
    private static final String C_ACTION_DEL = "DEL";
    private static final String C_ACTION_DISMISS = "DISMISS";

    private MissedAlertsDialog missedAlertsDialog;

    private MissedAlertsDialog getMissedAlertsDialog() {
        if (this.missedAlertsDialog == null) {
            this.missedAlertsDialog = new MissedAlertsDialog();
        }
        return this.missedAlertsDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);
        setActivityTitle(getResources().getString(R.string.heading_label_view_reminder));

        final Intent intent = getIntent();
        if (intent == null) {
            ToastHelper.showLong(ReminderView.this, "Reminder not found");
            finish();
            return;
        }

        alertModel = new ViewModelProvider(this, new AlertViewModelFactory(getIntent())).get(AlertModel.class);

        if (alertModel.isNew()) {
            ToastHelper.showLong(ReminderView.this, "Reminder not found");
            finish();
            return;
        }

        tv_reminder_time = findViewById(R.id.tv_reminder_time);
        tv_reminder_AmPm = findViewById(R.id.tv_reminder_AmPm);
        tv_reminder_date = findViewById(R.id.tv_reminder_date);
        tv_reminder_name = findViewById(R.id.tv_reminder_name);
        ly_note_summary_header = findViewById(R.id.ly_note_summary_header);
        tv_reminder_note = findViewById(R.id.tv_reminder_note);

        switchEnabled = findViewById(R.id.sw_reminder_enabled);
        switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUserInteracted())
                return;

            if (alertModel != null && !alertModel.isExpired()) {

                if (AppSettingsHelper.getInstance().isDisableAllReminders()) { // Ignore if its blocked globally

                    buttonView.setChecked(false);
                    ToastHelper.showShort(buttonView.getContext(), "All reminders are disabled in settings");

                } else {

                    if (alertModel.trySetEnabled(buttonView.getContext(), isChecked)) {
                        alertModel.saveAndSetAlert(buttonView.getContext(), true);
                        refresh();
                    } else {
                        buttonView.setChecked(false);
                    }
                }
            }
        });

        next_snooze = findViewById(R.id.tv_reminder_next_snooze);
        btn_reminder_dismiss = findViewById(R.id.btn_reminder_dismiss);
        btn_reminder_dismiss.setOnClickListener(v -> {
            alertModel.dismissByUser(v.getContext());
            refresh();
        });

        final LinearLayoutCompat lv_last_missed_alert = findViewById(R.id.lv_last_missed_alert);
        lv_last_missed_alert.setOnClickListener(v -> getMissedAlertsDialog().show(getSupportFragmentManager(), MissedAlertsDialog.TAG));
        tv_expired = findViewById(R.id.tv_expired);

        final AppCompatImageButton imgBtnShareNote = findViewById(R.id.imgBtnShareNote);
        imgBtnShareNote.setOnClickListener(v -> shareText(alertModel.getNote()));

        refresh();
    }

    @Override
    protected void onUIRefresh() {
        super.onUIRefresh();

        if (alertModel != null) {

            if (alertModel.getTimeModel().isHasScheduledTime()) {
                tv_reminder_time.setText(StringHelper.toTime(alertModel.getTimeModel().getScheduledTime()));
                tv_reminder_AmPm.setText(StringHelper.toAmPm(alertModel.getTimeModel().getScheduledTime()));
                tv_reminder_date.setText(StringHelper.toWeekdayDate(this, alertModel.getTimeModel().getScheduledTime()));
            } else {
                tv_reminder_time.setText(StringHelper.toTime(alertModel.getTimeModel().getTime()));
                tv_reminder_AmPm.setText(StringHelper.toAmPm(alertModel.getTimeModel().getTime()));
                tv_reminder_date.setText(StringHelper.toWeekdayDate(this, alertModel.getTimeModel().getTime()));
            }

            final AppCompatTextView tv_reminder_time_list_summary = findViewById(R.id.tv_reminder_time_list_summary);
            tv_reminder_time_list_summary.setText(alertModel.getTimeModel().toSpannableString(
                    getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor))));

            if (!StringHelper.isNullOrEmpty(alertModel.getName())) {
                tv_reminder_name.setVisibility(View.VISIBLE);
                tv_reminder_name.setText(alertModel.getName());
            }

            if (alertModel.getSnoozeModel().isSnoozed()) {
                next_snooze.setVisibility(View.VISIBLE);
                next_snooze.setText(StringHelper.toTimeAmPm(
                        alertModel.getSnoozeModel().getSnoozedTime(
                                alertModel.getTimeModel().getTime())));

                btn_reminder_dismiss.setVisibility(View.VISIBLE);

                final FragmentManager manager = getSupportFragmentManager();
                final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
                final FabContextMenu contextMenu = new FabContextMenu();

                final FabContextMenu.MenuItem deleteItem = contextMenu.getNewMenuItem(C_ACTION_DEL);
                deleteItem.src = R.drawable.ic_delete;
                deleteItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(deleteItem);

                final FabContextMenu.MenuItem editItem = contextMenu.getNewMenuItem(C_ACTION_EDIT);
                editItem.src = R.drawable.ic_edit;
                editItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(editItem);

                final FabContextMenu.MenuItem dismiss = contextMenu.getNewMenuItem(C_ACTION_DISMISS);
                dismiss.src = R.drawable.ic_reminder_dismiss;
                dismiss.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(dismiss);

                transaction.replace(R.id.bottomContextMenu, contextMenu);
                transaction.commit();
            } else {
                next_snooze.setVisibility(View.GONE);
                btn_reminder_dismiss.setVisibility(View.GONE);

                final FragmentManager manager = getSupportFragmentManager();
                final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
                final FabContextMenu contextMenu = new FabContextMenu();

                final FabContextMenu.MenuItem deleteItem = contextMenu.getNewMenuItem(C_ACTION_DEL);
                deleteItem.src = R.drawable.ic_delete;
                deleteItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(deleteItem);

                final FabContextMenu.MenuItem editItem = contextMenu.getNewMenuItem(C_ACTION_EDIT);
                editItem.src = R.drawable.ic_edit;
                editItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(editItem);

                transaction.replace(R.id.bottomContextMenu, contextMenu);
                transaction.commit();
            }

            if (alertModel.isReminder()) {

                switchEnabled.setVisibility(View.VISIBLE);
                tv_expired.setVisibility(View.GONE);
                switchEnabled.setChecked(alertModel.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());

                if (alertModel.getReminderModel().getTime() != null) {
                    tv_expired.setVisibility(View.VISIBLE);
                    //tv_expired.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeWarningColor)));
                    tv_expired.setText(
                            String.format(Locale.getDefault(), "Reminded for %s",
                                    StringHelper.toTimeWeekdayDate(this,
                                            alertModel.getReminderModel().getTime())));
                }

                if (alertModel.getReminderModel().isCompleted()) {
                    tv_expired.setVisibility(View.VISIBLE);
                    //tv_expired.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor)));
                    tv_expired.setText(
                            String.format(Locale.getDefault(), "Completed for %s",
                                    StringHelper.toTimeWeekdayDate(this,
                                            alertModel.getReminderModel().getTime())));

                    switchEnabled.setVisibility(View.VISIBLE);
                } else if (alertModel.isExpired()) {
                    tv_expired.setVisibility(View.VISIBLE);
                    tv_expired.setText(getString(R.string.label_expired));
                    //tv_expired.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor)));

                    switchEnabled.setVisibility(View.GONE);
                }
            } else {
                if (alertModel.isExpired()) {
                    switchEnabled.setVisibility(View.GONE);
                    tv_expired.setVisibility(View.VISIBLE);
                    //tv_expired.setTextColor(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor)));
                    tv_expired.setText(getString(R.string.label_expired));
                } else {
                    tv_expired.setVisibility(View.GONE);
                    switchEnabled.setVisibility(View.VISIBLE);
                    switchEnabled.setChecked(alertModel.isEnabled() && !AppSettingsHelper.getInstance().isDisableAllReminders());
                }
            }

            final AppCompatTextView tv_reminder_snooze_summary = findViewById(R.id.tv_reminder_snooze_summary);
            tv_reminder_snooze_summary.setText(alertModel.getSnoozeModel().toString());

            final AppCompatTextView tv_reminder_repeat_summary = findViewById(R.id.tv_reminder_repeat_summary);
            tv_reminder_repeat_summary.setText(alertModel.getRepeatModel().toString(this));

            final AppCompatTextView tv_reminder_tone_summary = findViewById(R.id.tv_reminder_tone_summary);
            final LinearLayoutCompat lv_alarm_tone_is_on = findViewById(R.id.lv_alarm_tone_is_on);

            if (alertModel.getRingingModel().isToneEnabled()) {

                lv_alarm_tone_is_on.setVisibility(View.VISIBLE);
                tv_reminder_tone_summary.setText(alertModel.getRingingModel().getRingToneUriSummary(this));

                final AppCompatTextView tv_alarm_volume = findViewById(R.id.tv_alarm_volume);

                if (alertModel.getRingingModel().isIncreaseVolumeGradually()) {

                    tv_alarm_volume.setText(String.format(Locale.ENGLISH, "%d to %s",
                            RingingModel.MINIMUM_INPUT_VOLUME_PERCENTAGE,
                            alertModel.getRingingModel().getAlarmVolumePercentage() == 0 ?
                                    "default" : alertModel.getRingingModel().getAlarmVolumePercentage() + "%"
                    ));

                } else {

                    tv_alarm_volume.setText(String.format(Locale.ENGLISH, "%s",
                            alertModel.getRingingModel().getAlarmVolumePercentage() == 0 ?
                                    "default" : alertModel.getRingingModel().getAlarmVolumePercentage() + "%")
                    );
                }

            } else {
                lv_alarm_tone_is_on.setVisibility(View.GONE);
                tv_reminder_tone_summary.setText(STATUS_OFF);
            }

            tv_reminder_tone_summary.setTextColor(alertModel.getRingingModel().isToneEnabled() ?
                    getResources().getColor(R.color.colorDimText) : getResources().getColor(R.color.colorDanger));

            final AppCompatTextView tv_reminder_vibrate = findViewById(R.id.tv_reminder_vibrate);

            if (alertModel.getRingingModel().isVibrationEnabled()) {
                tv_reminder_vibrate.setText(getResources().getStringArray(R.array.values_vibration_pattern)[RingingModel.convertToVibratePattern(alertModel.getRingingModel().getVibratePattern())]);
            } else {
                tv_reminder_vibrate.setText(STATUS_OFF);
            }

            tv_reminder_vibrate.setTextColor(alertModel.getRingingModel().isVibrationEnabled() ?
                    getResources().getColor(R.color.colorDimText) : getResources().getColor(R.color.colorDanger));

            if (!StringHelper.isNullOrEmpty(alertModel.getNote())) {
                ly_note_summary_header.setVisibility(View.VISIBLE);
                tv_reminder_note.setVisibility(View.VISIBLE);
                tv_reminder_note.setText(alertModel.getNote());
            } else {
                ly_note_summary_header.setVisibility(View.GONE);
                tv_reminder_note.setVisibility(View.GONE);
            }

            final LinearLayoutCompat lv_last_missed_alert = findViewById(R.id.lv_last_missed_alert);

            //btn_expand_missed_alerts.setVisibility(View.GONE);
            if (alertModel.getLastMissedTime() != null) {
                lv_last_missed_alert.setVisibility(View.VISIBLE);
                final AppCompatTextView tv_reminder_last_missed_time = findViewById(R.id.tv_reminder_last_missed_time);
                tv_reminder_last_missed_time.setText(StringHelper.toTimeWeekdayDate(this, alertModel.getLastMissedTime()));
            } else {
                lv_last_missed_alert.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isMenuExpanded) {
            final FabContextMenu contextMenu = (FabContextMenu) getSupportFragmentManager().findFragmentById(R.id.bottomContextMenu);
            if (contextMenu != null && contextMenu.getView() != null) {
                int[] leftTop = {0, 0};
                contextMenu.getView().getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + contextMenu.getView().getHeight();
                int right = left + contextMenu.getView().getWidth();
                if (ev.getX() > left && ev.getX() < right
                        && ev.getY() > top && ev.getY() < bottom) {
                    // Click on the input box area, keep the event that clicks EditText
                    return super.dispatchTouchEvent(ev);
                }
                contextMenu.collapse();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onFabContextMenuClick(boolean isExpand) {
        isMenuExpanded = isExpand;
    }

    @Override
    public void onFabContextMenuAction(String clickAction, String clickValue) {
        switch (clickAction) {
            case C_ACTION_DEL:
                alertModel.deleteAndCancelAlert(getApplicationContext());
                finish();
                break;
            case C_ACTION_EDIT:
                final Intent input_i = new Intent(getApplicationContext(), ReminderInput.class);
                AlertModel.setReminderIdInIntent(input_i, alertModel.getId());
                startActivity(input_i);
                finish();
                break;
            case C_ACTION_DISMISS:
                alertModel.dismissByUser(this);
                refresh();
                break;
        }
    }

    @Override
    public void onChangeMissedAlertsList(AlertModel model) {
        alertModel = model;
        refresh();
    }

    @Override
    public AlertModel onGetReminderModel() {
        alertModel = new ViewModelProvider(this, new AlertViewModelFactory(getIntent())).get(AlertModel.class);
        return alertModel;
    }
}