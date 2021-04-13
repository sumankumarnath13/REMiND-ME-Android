package com.example.remindme.ui.main;

import com.example.remindme.viewModels.ReminderSnoozeModel;

public interface IReminderSnoozeListener {
    void commitChanges(ReminderSnoozeModel model);

    ReminderSnoozeModel getSnoozeModel();
}
