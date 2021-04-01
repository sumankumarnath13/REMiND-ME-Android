package com.example.remindme.helpers;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;

public class ActivityHelper {
    public static void setTitle(AppCompatActivity activity, String activityTitle) {
        String divider = " \u2026 ";
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
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_info)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_gray1)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_success)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;
        char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //This is the end of fixed Title and dynamic position begins:
        if (title != null) {
            spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_gray2)), finalTitle.length() - title.length(), finalTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new RelativeSizeSpan(1.5f), finalTitle.length() - activityTitle.length(), finalTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), finalTitle.length() - title.length(), finalTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //spannable.setSpan(new UnderlineSpan(), finalTitle.length() - activityTitle.length(), finalTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        activity.getSupportActionBar().setTitle(spannable);

        //activity.getSupportActionBar().setTitle(spannable.toString());


        //Let the line below controlled by the theme and not to force the title to be shown by the code:
        //activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}
