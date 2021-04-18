package com.example.remindme.controllers;

import android.os.Binder;

import com.example.remindme.viewModels.ReminderModel;

public class AlertServiceBinder extends Binder {

    private final AlertService service;

    public AlertServiceBinder(AlertService hostService) {
        service = hostService;
    }

    public ReminderModel getServingReminder() {
        return service.getServingReminder();
    }

    public void snoozeByUser() {
        service.snoozeByUser();
    }

    public void dismiss() {
        service.dismiss();
    }

    public void setActivityOpen(boolean value) {
        service.setActivityOpen(value);
    }

}
