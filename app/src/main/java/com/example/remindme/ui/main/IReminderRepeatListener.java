package com.example.remindme.ui.main;
import com.example.remindme.viewModels.ReminderRepeatModel;

public interface IReminderRepeatListener {

    void commitChanges(ReminderRepeatModel repeatModel);

    void discardChanges();

    ReminderRepeatModel getRepeatModel();
}
