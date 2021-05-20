package com.example.remindme.ui.fragments.dialogFragments.common;

import java.util.Date;

public interface IDateTimePickerListener {
    void setDateTimePicker(String tag, Date dateTime);

    Date getDateTimePicker(String tag);

    Date getMinimumDateTime(String tag);
}
