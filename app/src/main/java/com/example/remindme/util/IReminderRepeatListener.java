package com.example.remindme.util;

public interface IReminderRepeatListener {
    void set(ReminderRepeatModel repeatModel, boolean isEOF);
    ReminderRepeatModel getRepeatModel();
}
