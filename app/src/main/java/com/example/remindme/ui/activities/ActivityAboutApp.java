package com.example.remindme.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remindme.R;
import com.example.remindme.helpers.ActivityHelper;

public class ActivityAboutApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ActivityHelper.setTitle(this, getResources().getString(R.string.activityAboutAppTitle));

        final Button btn_send_feedback = findViewById(R.id.btn_send_feedback);
        btn_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.putExtra(Intent.EXTRA_SUBJECT, "Habla");
                email.putExtra(Intent.EXTRA_TEXT, "Kebla");
                //email.setDataAndType(Uri.parse("mailto:sknath25@gmail.com"), "message/rfc822");
                email.setData(Uri.parse("mailto:sknath25@gmail.com")); // or just "mailto:" for blank
                //need this to prompts email client only
                email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        final Button btn_view_faq = findViewById(R.id.btn_view_faq);
        btn_view_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent faqIntent = new Intent(ActivityAboutApp.this, ActivityWebView.class);
                faqIntent.setAction("FAQ");
                faqIntent.putExtra(ActivityWebView.URL, "https://www.google.co.in");
                startActivity(faqIntent);
            }
        });

        final Button btn_view_license = findViewById(R.id.btn_view_license);
        btn_view_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent licenseIntent = new Intent(ActivityAboutApp.this, ActivityWebView.class);
                licenseIntent.setAction("LICENSE");
                licenseIntent.putExtra(ActivityWebView.URL, "https://www.google.co.in");
                startActivity(licenseIntent);
            }
        });

        final Button btn_share_app = findViewById(R.id.btn_share_app);
        btn_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "DING DONG friends ");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
}