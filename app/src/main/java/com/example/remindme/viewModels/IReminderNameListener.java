package com.example.remindme.viewModels;

public interface IReminderNameListener {
    void setName(String name, boolean isEOF);
    String getReminderName();
}
