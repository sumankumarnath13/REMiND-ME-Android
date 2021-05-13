package com.example.remindme.ui.fragments.common;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

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

    private enum MenuStates {
        New,
        Rendered,
    }

    public final class MenuItem {
        private MenuStates state = MenuStates.New;
        public int src;
        public int imageTint;
        public int backgroundTint;
        private String clickAction;

        public String getClickAction() {
            return clickAction;
        }

        public String clickValue;
        public String hintText;
    }

    public interface iFabContextMenuHost {
        void onFabContextMenuClick(String clickAction, String clickValue);
    }

    private iFabContextMenuHost host;
    private final List<Pair<MenuItem, FloatingActionButton>> menuItems = new ArrayList<>();

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private LinearLayoutCompat layoutCompat;
    private FloatingActionButton fabRoot;
    private boolean isHasMany;
    private boolean isExpanded;
    private boolean isRenderedOnce;

    public void setHost(iFabContextMenuHost host) {
        this.host = host;
    }

    public MenuItem getNewMenuItem(String clickAction) {
        final MenuItem item = new MenuItem();
        item.clickAction = clickAction;
        return item;
    }

    public void addMenu(Context context, final MenuItem item) {

        if (item == null || StringHelper.isNullOrEmpty(item.clickAction))
            return;

        for (int i = 0; i < menuItems.size(); i++) {
            if (item.clickAction.equals(menuItems.get(i).first.clickAction)) {
                return;
            }
        }

        final FloatingActionButton fab = new FloatingActionButton(context);
        menuItems.add(0, new Pair<>(item, fab));

        if (isRenderedOnce) {
            renderButtons(context, getView());
        }
    }

    public void removeMenu(final String clickAction) {

        if (StringHelper.isNullOrEmpty(clickAction))
            return;

        for (int i = 0; i < menuItems.size(); i++) {
            if (clickAction.equals(menuItems.get(i).first.clickAction)) {
                if (layoutCompat != null) {
                    layoutCompat.removeViewAt(i);
                }
                menuItems.remove(i);
                break;
            }
        }

        setMenuRoot(getView());
    }

    private void setMenuRoot(View view) {

        if (view == null)
            return;

        if (fabRoot == null) {
            fabRoot = view.findViewById(R.id.fabBtnRoot);
            fabRoot.setOnClickListener(v -> switchExpansion());
        }

        if (isExpanded) {
            switchExpansion();
        }

        if (menuItems.size() > 1) { // Root button for context menu at
            isHasMany = true;
            if (fabRoot != null) {
                fabRoot.setVisibility(View.VISIBLE);
            }
        } else { // Just one button
            isHasMany = false;
            if (fabRoot != null) {
                fabRoot.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void renderButtons(Context context, View view) {

        if (context == null)
            return;

        if (view == null)
            return;

        if (layoutCompat == null) {
            layoutCompat = view.findViewById(R.id.fabContextMenuLayout);
        }

        for (int i = 0; i < menuItems.size(); i++) {

            final MenuItem item = menuItems.get(i).first;
            final FloatingActionButton fab = menuItems.get(i).second;

            if (item.state == MenuStates.New) {

                final LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(
                        (int) getResources().getDimension(R.dimen.dp_8),
                        (int) getResources().getDimension(R.dimen.dp_8),
                        (int) getResources().getDimension(R.dimen.dp_8),
                        (int) getResources().getDimension(R.dimen.dp_8));
                //layoutParams.gravity = Gravity.RE;
                fab.setLayoutParams(layoutParams);

                fab.setVisibility(View.INVISIBLE);
                fab.setContentDescription(getString(R.string.missingImageDesc));
                fab.setImageDrawable(AppCompatResources.getDrawable(context, item.src));

                if (OsHelper.isLollipopOrLater()) {
                    if (item.imageTint != 0) {
                        fab.setImageTintMode(PorterDuff.Mode.SRC_ATOP);
                        fab.setImageTintList(AppCompatResources.getColorStateList(context, item.imageTint));
                    }
                }

                if (item.backgroundTint != 0) {
                    fab.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
                    fab.setBackgroundTintList(AppCompatResources.getColorStateList(context, item.backgroundTint));
                }

                fab.setOnClickListener(v -> {
                    if (host != null) {
                        host.onFabContextMenuClick(item.clickAction, item.clickValue);
                    }
                });

                layoutCompat.addView(fab, i);
                item.state = MenuStates.Rendered;
            }
        }

        setMenuRoot(view);

        isRenderedOnce = true;
    }

    private void switchExpansion() {
        if (isHasMany && fabRoot != null) {
            isExpanded = !isExpanded;
            if (isExpanded) {
                fabRoot.startAnimation(rotateOpen);
            } else {
                fabRoot.startAnimation(rotateClose);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fab_context_menu, container, false);

        rotateOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open_anim);
        rotateOpen.setDuration(117);
        rotateOpen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).second.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).second.startAnimation(fromBottom);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null && fabRoot != null) {
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
                    menuItems.get(i).second.startAnimation(toBottom);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null && fabRoot != null) {
                    fabRoot.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_context_menu, getActivity().getTheme()));
                }

                for (int i = 0; i < menuItems.size(); i++) {
                    menuItems.get(i).second.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fromBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom_anim);
        fromBottom.setDuration(60);

        toBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom_anim);
        toBottom.setDuration(60);

        renderButtons(this.getContext(), view);

        return view;
    }
}