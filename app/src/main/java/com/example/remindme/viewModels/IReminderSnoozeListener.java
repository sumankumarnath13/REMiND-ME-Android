package com.example.remindme.viewModels;

import com.example.remindme.viewModels.ReminderSnoozeModel;

public interface IReminderSnoozeListener {
    void set(ReminderSnoozeModel model, boolean isEOF);
    ReminderSnoozeModel getSnoozeModel();
}
