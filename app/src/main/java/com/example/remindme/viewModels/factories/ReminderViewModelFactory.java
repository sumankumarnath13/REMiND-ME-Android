package com.example.remindme.viewModels.factories;

import android.content.Intent;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.ReminderModel;

public class ReminderViewModelFactory implements ViewModelProvider.Factory {

    private final Intent intent;

    public ReminderViewModelFactory(Intent intentArg) {
        this.intent = intentArg;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        ReminderModel m = ReminderModel.getInstance(intent);
        if (m == null) {
            m = new ReminderModel();
        }
        return (T) m;

        //return (T) ReminderModel.getInstance(intent);
    }
}
