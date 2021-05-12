package com.example.remindme.ui.fragments.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.remindme.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FabContextMenu extends Fragment {

    public class MenuItem {
        public int src;
        public int tint;
        public String hintText;
        public int buttonTint;
        public String clickAction;
        public String clickValue;
    }

    public interface iFabContextMenuHost {
        void onFabContextMenuClick(String clickAction, String clickValue);
    }


    private iFabContextMenuHost host;
    private List<MenuItem> menuItems = new ArrayList<>();

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private FloatingActionButton fab_context_btn_root;
    private FloatingActionButton fab_context_btn_1;
    private FloatingActionButton fab_context_btn_2;

    private boolean isExpand;

    public FabContextMenu() {
    }

    public void setHost(iFabContextMenuHost host) {
        this.host = host;
    }

    public void addMenu(final MenuItem item) {
        this.menuItems.add((item));
    }

    private void setVisibility(boolean isExpand) {
        if (isExpand) {
            fab_context_btn_1.setVisibility(View.VISIBLE);
            fab_context_btn_2.setVisibility(View.VISIBLE);
        } else {
            fab_context_btn_1.setVisibility(View.GONE);
            fab_context_btn_2.setVisibility(View.GONE);
        }
    }

    private void setAnimation(boolean isExpand) {
        if (isExpand) {
            fab_context_btn_root.startAnimation(rotateOpen);
        } else {
            fab_context_btn_root.startAnimation(rotateClose);
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
                fab_context_btn_1.startAnimation(fromBottom);
                fab_context_btn_2.startAnimation(fromBottom);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null) {
                    // ic_expand_up is required NOT ic_expand_down. Because it's rotated 180 degree by the end of the animation
                    fab_context_btn_root.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_expand_up, getActivity().getTheme()));
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
                fab_context_btn_1.startAnimation(toBottom);
                fab_context_btn_2.startAnimation(toBottom);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (getActivity() != null) {
                    fab_context_btn_root.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_context_menu, getActivity().getTheme()));
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

        fab_context_btn_root = view.findViewById(R.id.fab_context_btn_root);
        fab_context_btn_1 = view.findViewById(R.id.fab_context_btn_1);
        fab_context_btn_1.setVisibility(View.GONE);
        fab_context_btn_2 = view.findViewById(R.id.fab_context_btn_2);
        fab_context_btn_2.setVisibility(View.GONE);

        fab_context_btn_root.setOnClickListener(v -> {
            isExpand = !isExpand;
            setVisibility(isExpand);
            setAnimation(isExpand);
        });

        fab_context_btn_1.setOnClickListener(v -> {
            isExpand = !isExpand;
            setVisibility(isExpand);
            setAnimation(isExpand);
            if (host != null) {
                host.onFabContextMenuClick("del", "del");
            }
        });

        fab_context_btn_2.setOnClickListener(v -> {
            isExpand = !isExpand;
            setVisibility(isExpand);
            setAnimation(isExpand);
            if (host != null) {
                host.onFabContextMenuClick("update", "update");
            }
        });

        return view;
    }
}