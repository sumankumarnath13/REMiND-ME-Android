package com.example.remindme.ui.main;
import com.example.remindme.viewModels.ReminderRepeatModel;

public interface IReminderRepeatListener {
    //void setChanges(ReminderRepeatModel repeatModel);
    void commitChanges(ReminderRepeatModel repeatModel);

    void discardChanges();

    ReminderRepeatModel getRepeatModel();
}
