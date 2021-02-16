package com.example.remindme.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;

public class UtilsActivity {
    public static void setTitle(AppCompatActivity activity){
        Spannable spannable = new SpannableString(activity.getResources().getString(R.string.app_label));
        int char_pos = 0;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_success)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_info)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_dark)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_warning)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_success)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        char_pos++;char_pos++;
        spannable.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.text_danger)), char_pos, char_pos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        activity.getSupportActionBar().setTitle(spannable);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}