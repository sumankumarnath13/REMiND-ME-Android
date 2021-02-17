package com.example.remindme.viewModels;

import com.example.remindme.viewModels.ReminderRepeatModel;

public interface IReminderRepeatListener {
    void set(ReminderRepeatModel repeatModel, boolean isEOF);
    ReminderRepeatModel getRepeatModel();
}
