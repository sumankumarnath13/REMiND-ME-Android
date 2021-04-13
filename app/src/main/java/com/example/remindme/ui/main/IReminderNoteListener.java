package com.example.remindme.ui.main;

public interface IReminderNoteListener {
    void setNote(String note, boolean isEOF);
    String getReminderNote();
}
