package com.example.remindme.ui.fragments.common;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.remindme.R;
import com.example.remindme.helpers.OsHelper;
import com.example.remindme.helpers.StringHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FabContextMenu extends Fragment {

    public final class MenuItem {
        private FloatingActionButton actionButton;
        public int src;
        public int imageTint;
        public int backgroundTint;
        private String clickAction;
        public String clickValue;
    }

    public interface iFabContextMenuListener {
        void onFabContextMenuClick(boolean isExpand);

        void onFabContextMenuAction(String clickAction, String clickValue);
    }

    private iFabContextMenuListener contextMenuListener;
    private final List<MenuItem> menuItems = new ArrayList<>();

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private boolean isExpanded;
    private LinearLayoutCompat layoutCompat;
    private FloatingActionButton fabRoot;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.contextMenuListener = (iFabContextMenuListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.menu_fragment_fab_context_menu, container, false);

        rotateOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open_anim);
        rotateOpen.setDuration(117);
        rotateOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).actionButton.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).actionButton.startAnimation(fromBottom);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null) {
                    // ic_expand_up is required NOT ic_expand_down. Because it's rotated 180 degree by the end of the animation
                    fabRoot.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_expand_up, getActivity().getTheme()));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rotateClose = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_close_anim);
        rotateClose.setDuration(117);
        rotateClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).actionButton.startAnimation(toBottom);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null) {
                    fabRoot.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_context_menu, getActivity().getTheme()));
                }

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).actionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fromBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom_anim);
        fromBottom.setDuration(63);

        toBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom_anim);
        toBottom.setDuration(63);

        layoutCompat = view.findViewById(R.id.fabContextMenuLayout);

        fabRoot = view.findViewById(R.id.fabBtnRoot);

        return view;
    }

    public MenuItem getNewMenuItem(String clickAction) {
        final MenuItem item = new MenuItem();
        item.clickAction = clickAction;
        return item;
    }

    public void addMenu(final MenuItem item) {

        if (item == null || StringHelper.isNullOrEmpty(item.clickAction))
            return;

        for (int i = 0; i < menuItems.size(); i++) {
            if (item.clickAction.equals(menuItems.get(i).clickAction)) {
                return;
            }
        }

        menuItems.add(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        renderButtons(this.getContext());
    }

    private void renderButtons(Context context) {

        for (int i = 0; i < menuItems.size(); i++) {

            final MenuItem item = menuItems.get(i);
            item.actionButton = new FloatingActionButton(context);
            item.actionButton.setContentDescription(getString(R.string.missingImageDesc));
            item.actionButton.setImageDrawable(AppCompatResources.getDrawable(context, item.src));

            if (OsHelper.isLollipopOrLater()) {
                if (item.imageTint != 0) {
                    item.actionButton.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
                    item.actionButton.setImageTintList(AppCompatResources.getColorStateList(context, item.imageTint));
                }
            }

            if (item.backgroundTint != 0) {
                item.actionButton.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                item.actionButton.setBackgroundTintList(AppCompatResources.getColorStateList(context, item.backgroundTint));
            }

            item.actionButton.setOnClickListener(v -> {
                if (contextMenuListener != null) {
                    contextMenuListener.onFabContextMenuAction(item.clickAction, item.clickValue);
                }
            });

            final LinearLayoutCompat.LayoutParams layoutParams =
                    new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(
                    0,
                    0,
                    0,
                    (int) getResources().getDimension(R.dimen.dp_8));
            item.actionButton.setLayoutParams(layoutParams);
            item.actionButton.setVisibility(View.GONE);

            layoutCompat.addView(item.actionButton, i);
        }

        if (menuItems.size() > 1) { // Root button for context menu at

            fabRoot.setVisibility(View.VISIBLE);

            fabRoot.setOnClickListener(v -> switchExpansion());

        } else { // Just one button

            fabRoot.setVisibility(View.GONE);

            if (menuItems.size() > 0) {

                menuItems.get(0).actionButton.setVisibility(View.VISIBLE);

            }
        }
    }

    public void switchExpansion() {

        isExpanded = !isExpanded;

        if (isExpanded) {

            fabRoot.startAnimation(rotateOpen);

        } else {

            fabRoot.startAnimation(rotateClose);

        }

        if (this.contextMenuListener != null) {

            this.contextMenuListener.onFabContextMenuClick(isExpanded);

        }

    }

    public void collapse() {

        if (isExpanded) {

            isExpanded = false;

            fabRoot.startAnimation(rotateClose);

        }
    }
}