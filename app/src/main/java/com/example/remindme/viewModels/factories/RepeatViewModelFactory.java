package com.example.remindme.viewModels.factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.RepeatModel;

public class RepeatViewModelFactory implements ViewModelProvider.Factory {

    private final RepeatModel source;

    public RepeatViewModelFactory(final RepeatModel source) {
        this.source = source;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) source.copy();
    }

}
