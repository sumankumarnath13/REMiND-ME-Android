package com.example.remindme.helpers;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;

public class ActivityHelper {

    public static void setTitle(AppCompatActivity activity, String activityTitle) {
        //String divider = " \u2026 ";
        String divider = "   >   ";
        String title = null;
        String finalTitle;
        if (activityTitle != null && activityTitle.length() > 0) {
            title = divider + activityTitle;
            finalTitle = activity.getResources().getString(R.string.app_label) + title;
        } else {
            finalTitle = activity.getResources().getString(R.string.app_label);
        }


        Spannable spannable = new SpannableString(finalTitle);
        int char_pos = 0;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_success)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        //char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        //char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_info)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        //char_pos++;

        if (AppSettingsHelper.getInstance().getTheme() == AppSettingsHelper.Themes.LIGHT) {
            spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_soothing_light)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_soothing)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        char_pos++;
        //char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        //char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
//        char_pos++;
//        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_success)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        //char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //This is the end of fixed Title and dynamic position begins:
        if (title != null) {
            spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_dim)), finalTitle.length() - title.length(), finalTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.0f), finalTitle.length() - activityTitle.length(), finalTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), finalTitle.length() - title.length(), finalTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new UnderlineSpan(), finalTitle.length() - activityTitle.length(), finalTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        final ActionBar bar = activity.getSupportActionBar();
//        final View view = bar.getCustomView();
//        if (view != null) {
//            final TextView tv_toolbar_app_title = bar.getCustomView().findViewById(R.id.tv_toolbar_app_title);
//            tv_toolbar_app_title.setText("OLA HOLA");
//
//            final TextView tv_toolbar_activity_title = bar.getCustomView().findViewById(R.id.tv_toolbar_activity_title);
//            tv_toolbar_activity_title.setText("HOLA OLA");
//        }

        if (bar != null) {
            bar.setTitle(spannable);
        }

        //activity.getSupportActionBar().setTitle(spannable.toString());


        //Let the line below controlled by the theme and not to force the title to be shown by the code:
        //activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public static void shareText(Context context, String value) {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }
}
