package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.SnoozeModel;

public class SnoozeViewModelFactory implements ViewModelProvider.Factory {

    private final SnoozeModel source;

    public SnoozeViewModelFactory(SnoozeModel source) {
        this.source = source;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) source.copy();
    }

}
