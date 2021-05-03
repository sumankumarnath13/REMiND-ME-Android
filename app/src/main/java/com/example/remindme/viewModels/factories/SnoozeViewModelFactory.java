package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.SnoozeModel;

public class SnoozeViewModelFactory implements ViewModelProvider.Factory {

    private final ReminderModel reminderModel;

    public SnoozeViewModelFactory(ReminderModel parent) {
        reminderModel = parent;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        SnoozeModel copy = reminderModel.getSnoozeModel().copy();
        return (T) copy;
    }

}
