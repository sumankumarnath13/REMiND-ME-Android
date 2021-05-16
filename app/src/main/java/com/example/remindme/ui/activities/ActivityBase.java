package com.example.remindme.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;

import androidx.appcompat.app.ActionBar;

import com.example.remindme.R;
import com.example.remindme.helpers.AppSettingsHelper;
import com.example.remindme.ui.RefreshableActivity;

public class ActivityBase extends RefreshableActivity {

    private boolean isThemeChangeReceiverRegistered = false;

    private final BroadcastReceiver themeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Settings.THEME_CHANGE_INTENT_ACTION.equals(intent.getAction())) {
                recreate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (this instanceof Main) {

            final IntentFilter intentFilter = new IntentFilter(Settings.THEME_CHANGE_INTENT_ACTION);
            registerReceiver(themeChangeReceiver, intentFilter);
            isThemeChangeReceiverRegistered = true;

            if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
                setTheme(R.style.NoActionBarLightStyle);
            } else {
                setTheme(R.style.NoActionBarStyle);
            }

        } else {

            if (this instanceof Settings) {

                final IntentFilter intentFilter = new IntentFilter(Settings.THEME_CHANGE_INTENT_ACTION);
                registerReceiver(themeChangeReceiver, intentFilter);
                isThemeChangeReceiverRegistered = true;
            }

            if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
                setTheme(R.style.LightTheme);
            } else {
                setTheme(R.style.BlackTheme);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0f);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;

    }

    @Override
    protected void onDestroy() {

        if (isThemeChangeReceiverRegistered) {
            unregisterReceiver(themeChangeReceiver);
            isThemeChangeReceiverRegistered = false;
        }

        super.onDestroy();
    }

    @Override
    protected void onUIRefresh() {

    }

    public void shareText(String value) {

        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    public int resolveRefAttributeResourceId(int refAttributeId) {

        final Resources.Theme theme = getTheme();

        final TypedValue typedValue = new TypedValue();

        if (theme.resolveAttribute(refAttributeId, typedValue, true)) {
            return typedValue.resourceId;
        }

        return -1;
    }

    public void setActivityTitle(final String activitySubTitle) {

        final Spannable spannable = new SpannableString(getResources().getString(R.string.app_label));

        int char_pos = 0;

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeWarningColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeCuriosityColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDimText))),
                char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeWarningColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ++char_pos;

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeSuccessColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(resolveRefAttributeResourceId(R.attr.themeDangerColor))), char_pos, ++char_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(spannable);
            bar.setSubtitle(activitySubTitle);
        }
    }
}
