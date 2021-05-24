package com.example.remindme.ui.fragments.common;

import com.example.remindme.viewModels.AlertModel;

import java.util.List;

public interface iSelectionControl {
    boolean isSelectable();

    void dismissSelectable();

    boolean isAllSelected();

    void selectAll();

    boolean isNoneSelected();

    void selectNone();

    int getSelectedCount();

    List<AlertModel> getSelected();

    void notifySelectionChange();

    int size();

    void removeAllSelected();
}
