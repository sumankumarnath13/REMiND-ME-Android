package com.example.remindme.viewModels;

public interface IReminderNoteListener {
    void setNote(String note, boolean isEOF);
    String getReminderNote();
}
