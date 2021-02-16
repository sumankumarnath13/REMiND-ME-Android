package com.example.remindme.util;

public interface IReminderSnoozeListener {
    void set(ReminderSnoozeModel model, boolean isEOF);
    ReminderSnoozeModel getSnoozeModel();
}
