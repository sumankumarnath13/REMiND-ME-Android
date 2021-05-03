package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.ReminderModel;

public class RepeatViewModelFactory implements ViewModelProvider.Factory {

    private final ReminderModel reminderModel;

    public RepeatViewModelFactory(ReminderModel parent) {
        reminderModel = parent;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) reminderModel.getRepeatModel().copy();
    }

}
