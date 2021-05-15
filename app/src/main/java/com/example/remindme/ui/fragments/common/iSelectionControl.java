package com.example.remindme.ui.fragments.common;

import com.example.remindme.viewModels.ReminderModel;

import java.util.List;

public interface iSelectionControl {
    boolean isSelectable();

    void setSelectable(boolean value);

    boolean isAllSelected();

    void selectAll();

    boolean isNoneSelected();

    void selectNone();

    int getSelectedCount();

    List<ReminderModel> getSelected();

    void notifyChange();

    int size();

    void notifySelectedAsDeleted();
}
