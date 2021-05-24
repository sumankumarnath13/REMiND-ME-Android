package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.AlertModel;
import com.example.remindme.viewModels.TimeModel;

public class TimeViewModelFactory implements ViewModelProvider.Factory {

    private final AlertModel alertModel;

    public TimeViewModelFactory(AlertModel parent) {
        alertModel = parent;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        TimeModel copy = alertModel.getTimeModel().copy();
        return (T) copy;
    }

}
