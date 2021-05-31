package com.example.remindme.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.remindme.R;
import com.example.remindme.ui.fragments.common.FabContextMenu;
import com.example.remindme.ui.fragments.common.iSelectionControl;
import com.example.remindme.ui.main.AdapterRecyclerReminder;
import com.example.remindme.ui.main.AdapterSectionsPager;
import com.example.remindme.viewModels.AlertModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ActivityMain extends ActivityBase implements FabContextMenu.iFabContextMenuListener, AdapterRecyclerReminder.iDataChangeListener {

    private static final String C_ACTION_NEW = "NEW";
    private static final String C_ACTION_EDIT = "EDIT";
    private static final String C_ACTION_DEL = "DEL";
    private static final String C_ACTION_SELECT_ALL = "SELECT_ALL";
    private static final String C_ACTION_SELECT_NONE = "SELECT_NONE";

    private iSelectionControl selectionControl;
    private boolean isMenuExpanded;
    private boolean isTabMoving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AdapterSectionsPager adapterSectionsPager = new AdapterSectionsPager(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapterSectionsPager);
        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isTabMoving) {
                    isTabMoving = false;
                    final CountDownTimer timer = new CountDownTimer(300, 300) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (selectionControl != null && selectionControl.isSelectable()) {
                                selectionControl.dismissSelectable();
                            }
                        }
                    };
                    timer.start();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (selectionControl != null && selectionControl.isSelectable()) {
                    isTabMoving = true;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setActivityTitle("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        onSelectionChange(null); // Set the initial Add button
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
            case R.id.action_settings:
                Intent settings_intent = new Intent(this, Settings.class);
                startActivity(settings_intent);
                break;

            case R.id.action_about_app:
                Intent aboutAppIntent = new Intent(this, AboutApp.class);
                startActivity(aboutAppIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (selectionControl != null && selectionControl.isSelectable()) {
            selectionControl.dismissSelectable();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isMenuExpanded) {
            final FabContextMenu contextMenu = (FabContextMenu) getSupportFragmentManager().findFragmentById(R.id.bottomContextMenu);
            if (contextMenu != null && contextMenu.getView() != null) {
                int[] leftTop = {0, 0};
                contextMenu.getView().getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + contextMenu.getView().getHeight();
                int right = left + contextMenu.getView().getWidth();
                if (ev.getX() > left && ev.getX() < right
                        && ev.getY() > top && ev.getY() < bottom) {
                    // Click on the input box area, keep the event that clicks EditText
                    return super.dispatchTouchEvent(ev);
                }
                contextMenu.collapse();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onFabContextMenuClick(boolean isExpand) {
        isMenuExpanded = isExpand;
    }

    @Override
    public void onFabContextMenuAction(String clickAction, String clickValue) {
        switch (clickAction) {
            case C_ACTION_NEW:
                final Intent addNewReminderActivity = new Intent(ActivityMain.this, ReminderInput.class);
                startActivity(addNewReminderActivity);
                break;
            case C_ACTION_EDIT:
                final Intent input_i = new Intent(getApplicationContext(), ReminderInput.class);
                AlertModel.setReminderIdInIntent(input_i, selectionControl.getSelected().get(0).getId());
                startActivity(input_i);
                break;
            case C_ACTION_DEL:
                if (selectionControl != null) {
                    final List<AlertModel> selectedReminders = selectionControl.getSelected();
                    for (int i = 0; i < selectedReminders.size(); i++) {
                        selectedReminders.get(i).deleteAndCancelAlert(this);
                    }
                    selectionControl.removeAllSelected();
                }
                break;
            case C_ACTION_SELECT_ALL:
                if (selectionControl != null) {
                    selectionControl.selectAll();
                }
                break;
            case C_ACTION_SELECT_NONE:
                if (selectionControl != null) {
                    selectionControl.selectNone();
                }
                break;
        }
    }

    @Override
    public void onSelectionChange(final iSelectionControl selectionControl) {

        this.selectionControl = selectionControl;

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);
        final FabContextMenu contextMenu = new FabContextMenu();

        if (selectionControl == null || !selectionControl.isSelectable()) {

            final FabContextMenu.MenuItem newItem = contextMenu.getNewMenuItem(C_ACTION_NEW);
            newItem.src = R.drawable.ic_add;
            newItem.imageTint = android.R.color.white;
            newItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeAccentColor);

            contextMenu.addMenu(newItem);

        } else {

            if (selectionControl.size() > 0) {

                if (selectionControl.isAllSelected()) {

                    final FabContextMenu.MenuItem selectNone = contextMenu.getNewMenuItem(C_ACTION_SELECT_NONE);
                    selectNone.src = R.drawable.ic_check_off;
                    selectNone.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                    contextMenu.addMenu(selectNone);

                } else {

                    final FabContextMenu.MenuItem selectAll = contextMenu.getNewMenuItem(C_ACTION_SELECT_ALL);
                    selectAll.src = R.drawable.ic_check_on;
                    selectAll.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                    contextMenu.addMenu(selectAll);

                }

            }

            if (selectionControl.getSelectedCount() == 1) {

                final FabContextMenu.MenuItem deleteItem = contextMenu.getNewMenuItem(C_ACTION_DEL);
                deleteItem.src = R.drawable.ic_delete;
                deleteItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(deleteItem);

                final FabContextMenu.MenuItem editItem = contextMenu.getNewMenuItem(C_ACTION_EDIT);
                editItem.src = R.drawable.ic_edit;
                editItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(editItem);

            } else if (selectionControl.getSelectedCount() > 1) {

                final FabContextMenu.MenuItem deleteItem = contextMenu.getNewMenuItem(C_ACTION_DEL);
                deleteItem.src = R.drawable.ic_delete;
                deleteItem.backgroundTint = resolveRefAttributeResourceId(R.attr.themeDisabledControlColor);
                contextMenu.addMenu(deleteItem);

            }

        }

        transaction.replace(R.id.bottomContextMenu, contextMenu);
        transaction.commit();

    }

}