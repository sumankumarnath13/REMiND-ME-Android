package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.ReminderModel;
import com.example.remindme.viewModels.TimeModel;

public class TimeViewModelFactory implements ViewModelProvider.Factory {

    private final ReminderModel reminderModel;

    public TimeViewModelFactory(ReminderModel parent) {
        reminderModel = parent;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        TimeModel copy = reminderModel.getTimeModel().copy();
        return (T) copy;
    }

}
