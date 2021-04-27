package com.example.remindme.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;
import com.example.remindme.helpers.DeviceHelper;
import com.example.remindme.helpers.StringHelper;

import java.util.Calendar;

public class ActivityAboutApp extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ActivityHelper.setTitle(this, getResources().getString(R.string.activityAboutAppTitle));

        final Button btn_send_feedback = findViewById(R.id.btn_send_feedback);
        btn_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String subject = String.format("App feedback - %s", StringHelper.toWeekdayDate(Calendar.getInstance().getTime()));
                final String emailText = String.format("Brand : %s\nModel : %s\nSystem : %s\nUpdates : %s\n---\n",
                        DeviceHelper.getInstance().getBrand(),
                        DeviceHelper.getInstance().getModel(),
                        DeviceHelper.getInstance().getOperatingSystemSignature(),
                        DeviceHelper.getInstance().getOperatingSystemUpdateSignature());

                final Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:sknath25@gmail.com")); // or just "mailto:" for blank
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, emailText);
                email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(Intent.createChooser(email, null));
            }
        });

        final Button btn_view_faq = findViewById(R.id.btn_view_faq);
        btn_view_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent faqIntent = new Intent(Intent.ACTION_VIEW);
                faqIntent.setData(Uri.parse("https://www.google.co.in"));
                startActivity(faqIntent);
            }
        });

        final Button btn_view_license = findViewById(R.id.btn_view_license);
        btn_view_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent licenseIntent = new Intent(Intent.ACTION_VIEW);
                licenseIntent.setData(Uri.parse("https://www.google.co.in"));
                startActivity(licenseIntent);
            }
        });

        final Button btn_share_app = findViewById(R.id.btn_share_app);
        btn_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.shareText(ActivityAboutApp.this, "DING DONG Friends");
            }
        });
    }

}