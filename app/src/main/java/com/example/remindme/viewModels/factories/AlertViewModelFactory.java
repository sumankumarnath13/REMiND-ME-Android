package com.example.remindme.viewModels.factories;

import android.content.Intent;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.remindme.viewModels.AlertModel;

public class AlertViewModelFactory implements ViewModelProvider.Factory {

    private final Intent intent;
    private final AlertModel alertModel;

    public AlertViewModelFactory(Intent intentArg) {
        this.intent = intentArg;
        this.alertModel = null;
    }

    public AlertViewModelFactory(AlertModel model) {
        this.intent = null;
        this.alertModel = model;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (this.alertModel == null) {
            AlertModel m = AlertModel.getInstance(intent);
            if (m == null) {
                m = new AlertModel();
            }
            return (T) m;
        } else {
            return (T) this.alertModel;
        }
    }
}
